android-network-fundamentals
============================

This project is created to solve routine task of implemeting service-based architecture for execution of network/long time operations at the Android applications.

Dependencies
------------

Test project uses [Diego][0] to implement `ContentProvider` as simple as possible. It creates `ContentProvider`, relationships, and all that `UriMatcher` stuff by processing a provided static `Contract` class with entities and field declarations. You can look at the [RequestContract] (https://github.com/shaubert/android-network-fundamentals/blob/master/test/src/com/shaubert/net/test/SimpleContentProvider.java) declaration for example.

[0]: http://code.google.com/p/diego/        "Diego"

How it works
------------

Each operation or request has it's own execution logic and separated state:
`
  public interface Request {

      RequestState getState();
    
      void execute(ExecutionContext executionContext) throws Exception;
    
      boolean isCancelled();
    
      void cancel();
    
  }
`
The `RequestState` at the base implementation contains unique identifier, executuion state, progress, cancellation mark, and JSON entity for storing extra attributes. It's separated from request in order to simplify persistance. Also `RequestState` provides `ContentValues getValues()` method to store the state in the database. In fact, `RequestState` is `ContentValues`.

After you create a `Request` you should register it in the `Journal`:

  public class DefaultJournal implements Journal<RequestBase> {
    
      ...

      @Override
      public void register(RequestBase request) {
          request.getState().setStatus(RequestStatus.NOT_STARTED);
          repository.insert(request);
          executorBridge.queueRequest(request.getState().getId());
      };
      
      ...
  }
  
The registration includes storing request in the repository (receiving id) and queueing the request. The default implementation of `ExecutorBridge` will create new `Intent` with provided id and start the `RequestExecutor`. The `RequestExecutor` is based on the `IntentService` so it has a single `HandlerThread` but with modified `onStart()` logic to process intents with cancel action. Requests executes consequentially in the separate thread. In addition, `RequestExecutor` changes request's state as it follows the execution process:

  public class RequestExecutor extends Service implements ExecutionContext {

      ...
      
      protected void execute(RequestBase request) {
          request.getState().setStatus(RequestStatus.PROCESSING);
          repository.update(request);

          RequestStatus status = RequestStatus.FINISHED;
          try {
              request.execute(this);
          } catch (Exception e) {
              Log.w(getClass().getSimpleName(), null, e);
              status = RequestStatus.FINISHED_WITH_ERRORS;
          }
          
          request.getState().setStatus(status);
          repository.update(request);
      }
      
      ...
      
  }
  
All communications with the storage goes through the `Repositoty<T>` interface:

  public interface Repository<T> {

      T select(long id);
      
      void insert(T entity);
      
      void update(T entity);
      
      void delete(T entity);
        
      ...
      
  }
  
The default implementation `RequestRepository` delegates calls to the `ContentResolver`. Additionally it adds extra keys to the `ContentValues` received from the `RequestStateBase`:

    public static final String CLASS_NAME_KEY = "_class_name";
    public static final String CREATION_TIME_KEY = "_creation_time";
    public static final String UPDATE_TIME_KEY = "_update_time";
    
`CLASS_NAME_KEY` used to recreate request from the cursor row. It's expected that Request class has constructor with a single `RequestStateBase` argument.