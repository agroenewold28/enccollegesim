package com.endicott.edu.service; /**
 * Created by abrocken on 7/8/2017.
 */

import com.endicott.edu.datalayer.CollegeDao;
import com.endicott.edu.exceptions.DataNotFoundException;
import com.endicott.edu.models.CollegeModel;
import com.endicott.edu.simulators.CollegeManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

// The Java class will be hosted at the URI path "/helloworld"
@Path("/college")
public class CollegeService {
    CollegeDao collegeDao = new CollegeDao();

    /**
     * Retrieve a college simulation that already exists.
     *
     * @param runId the unique id for the simulation run
     * @return college in JSON format
     */
    @GET
    @Path("/{runId}")
    @Produces(MediaType.APPLICATION_JSON)
    public CollegeModel getCollege(@PathParam("runId") String runId) {
        CollegeModel college = collegeDao.getCollege(runId);
        return college;
    }

    /**
     * Create a new college simulation run.
     * The body of the post is ignored.
     * The server determines the initial settings.
     *
     * @param runId the unique id for the simulation run
     * @return college in JSON format
     */
    @POST
    @Path("/{runId}")
    @Produces(MediaType.APPLICATION_JSON)
    public CollegeModel postCollege(@PathParam("runId") String runId) {
        return CollegeManager.establishCollege(runId);
    }

    /**
     * Delete an existing simulation run.
     *
     * @param runId the unique id for the simulation run
     * @return a useless plain text message
     */
    @DELETE
    @Path("/{runId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteCollege(@PathParam("runId") String runId) {
        CollegeManager president = new CollegeManager();
        president.sellCollege(runId);
        return "College might have been deleted.\n";
    }

    /**
     * Update an existing simulation run.
     *
     * @param runId the unique id for the simulation run
     * @return college in JSON format
     */
    @PUT
    @Path("/{runId}/{command}")
    @Produces(MediaType.APPLICATION_JSON)
    public CollegeModel putCollege(@PathParam("runId") String runId, @PathParam("command") String command) {
        System.out.println("College command: " + command);

        if (command.equalsIgnoreCase("nextDay")) {
            return CollegeManager.nextDay(runId);
        } else {
            throw new DataNotFoundException("Unknown command.");
        }
    }

    /**
     * A simple test service.
     *
     * @return A success message.
     */
    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String getTest() {
        return "Success.\n";
    }
}