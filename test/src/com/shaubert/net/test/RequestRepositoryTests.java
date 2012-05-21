package com.shaubert.net.test;

import com.shaubert.net.core.DefaultRequestRecreator;
import com.shaubert.net.core.RequestBase;
import com.shaubert.net.core.RequestRepositoryOnContentResolver;
import com.shaubert.net.core.RequestStateBase;
import com.shaubert.net.nutshell.RequestStatus;

import android.test.ProviderTestCase2;

import java.util.ArrayList;
import java.util.List;

public class RequestRepositoryTests extends ProviderTestCase2<SimpleContentProvider> {

    private RequestRepositoryOnContentResolver repository;
    
    public RequestRepositoryTests() {
        super(SimpleContentProvider.class, "test");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        repository = new RequestRepositoryOnContentResolver(getMockContext(), new DefaultRequestRecreator(getContext()), SimpleContentProvider.RequestContract.Request.URI);
    }
    
    protected List<RequestBase> generateRequests(int count) {
        List<RequestBase> requests = new ArrayList<RequestBase>();
        for (int i = 0; i < count; i++) {
            requests.add(new SimpleRequest());
        }
        return requests;
    }
    
    public void testInsert() throws Exception {
        SimpleRequest request = new SimpleRequest();
        repository.insert(request);
        assertTrue(request.getState().getId() > 0);
    }
    
    public void testBulkInsert() throws Exception {
        List<RequestBase> requests = generateRequests(3);
        repository.insert(requests);
        assertTrue(requests.get(0).getState().getId() > 0);
        assertTrue(requests.get(1).getState().getId() > 0);
        assertTrue(requests.get(2).getState().getId() > 0);
        
        assertTrue(requests.get(0).getState().getId() != requests.get(1).getState().getId());
        assertTrue(requests.get(1).getState().getId() != requests.get(2).getState().getId());
    }
    
    public void testSelect() throws Exception {
        SimpleRequest request = new SimpleRequest();
        repository.insert(request);
        
        SimpleRequest request2 = (SimpleRequest)repository.select(request.getState().getId());
        assertEquals(request.getState().getId(), request2.getState().getId());
    }
    
    public void testSelectState() throws Exception {
        SimpleRequest request = new SimpleRequest();
        request.getState().put("test", "value");
        repository.insert(request);
        
        RequestStateBase state = (RequestStateBase)repository.selectState(request.getState().getId());
        assertEquals("value", state.getString("test"));
    }
    
    public void testUpdate() throws Exception {
        SimpleRequest request = new SimpleRequest();
        repository.insert(request);
        request.getState().setStatus(RequestStatus.FINISHED);
        repository.update(request);
        
        RequestBase request2 = repository.select(request.getState().getId());
        assertEquals(RequestStatus.FINISHED, request2.getState().getStatus());
    }

    public void testBulkUpdate() throws Exception {
        List<RequestBase> requests = generateRequests(3);
        repository.insert(requests);
        
        for (RequestBase request : requests) {
            request.getState().put("updated", true);
        }
        repository.update(requests);
        
        assertEquals(true, repository.select(requests.get(0).getState().getId()).getState().getBoolean("updated", false));
        assertEquals(true, repository.select(requests.get(1).getState().getId()).getState().getBoolean("updated", false));
        assertEquals(true, repository.select(requests.get(2).getState().getId()).getState().getBoolean("updated", false));
    }
    
    public void testDelete() throws Exception {
        SimpleRequest request = new SimpleRequest();
        repository.insert(request);
        repository.delete(request);
        assertNull(repository.select(request.getState().getId()));
    }

    public void testBulkDelete() throws Exception {
        List<RequestBase> requests = generateRequests(3);
        repository.insert(requests);
        repository.delete(requests);
        
        assertNull(repository.select(requests.get(0).getState().getId()));
        assertNull(repository.select(requests.get(1).getState().getId()));
        assertNull(repository.select(requests.get(2).getState().getId()));
    }
    
}
