package com.endicott.edu.service;

import com.endicott.edu.datalayer.DormitoryDao;
import com.endicott.edu.models.DormitoryModel;
import com.endicott.edu.simulators.CollegeManager;
import com.endicott.edu.simulators.DormManager;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

// The Java class will be hosted at the URI path "/finances"
@Path("/dorms")
public class DormServices {
    private DormitoryDao dao = new DormitoryDao();



    /**
     * Create a new dorm.
     * Notice that it consumes "text plain".  It really should be  APPLICATION_JSON
     * but having trouble getting this to work.
     *
     * @return college in JSON format
     */
    @POST
    @Path("/{runId}/{dormName}/{dormType}")
    @Produces(MediaType.APPLICATION_JSON)
    public DormitoryModel addDorm(@PathParam("runId") String runId,@PathParam("dormName") String dormName,
                                  @PathParam("dormType") String dormType) {

        if (!CollegeManager.doesCollegeExist(runId)) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        // Override some fields
        DormitoryDao dormitoryDao = new DormitoryDao();
        return (DormManager.createDorm(runId, dormName, dormType));
    }

    /**
     * Get a list of the dorms that are in the college.
     *
     * @param runId simulation ID
     * @return JSON formatted list.
     */
    @GET
    @Path("/{runId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DormitoryModel> getDorms(@PathParam("runId") String runId) {
        return dao.getDorms(runId);
    }


}

//    @GET
//    @Produces("text/plain")
//    //@Produces(MediaType.APPLICATION_XML)
//    public DormitoryModel getById() {
//        DormitoryModel acct = dao.getFinances("8");
//        return acct;
//    }
//    @GET
//    @Path("/{id}")
//    @Produces(MediaType.APPLICATION_XML)
//    public DormitoryModel getById(final @PathParam("id") String id) {
//        DormitoryModel acct = dao.getFinances(id);
//        return acct;
//    }
//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    public String getFinances() {
//        DormitoryModel bankAccount = dao.getFinances("8");
//        return bankAccount.toJson();
//    }
//- works 7/19 returns xml displayable in the browser
//    @GET
//    @Produces(MediaType.APPLICATION_XML)
//    public DormitoryModel getFinances() {
//        DormitoryModel bankAccount = dao.getFinances("8");
//        return bankAccount;
//    }
