/*
 *Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 *
 *This program is free software; you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation; either version 2 of the License, or
 *(at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 *USA
 *
 *http://www.gnu.org/licenses/gpl.txt
 */
package org.lamsfoundation.lams.monitoring.service;

import java.io.IOException;
import java.util.LinkedList;

import org.lamsfoundation.lams.AbstractLamsTestCase;
import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.learningdesign.GateActivity;
import org.lamsfoundation.lams.learningdesign.Grouping;
import org.lamsfoundation.lams.learningdesign.dao.IActivityDAO;
import org.lamsfoundation.lams.learningdesign.dao.hibernate.ActivityDAO;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.lesson.dao.ILessonDAO;
import org.lamsfoundation.lams.lesson.dao.hibernate.LessonDAO;
import org.lamsfoundation.lams.tool.service.LamsToolServiceException;
import org.lamsfoundation.lams.usermanagement.Organisation;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.service.IUserManagementService;


/**
 * 
 * @author Jacky Fang 9/02/2005
 * @author Manpreet Minhas
 */
public class TestMonitoringService extends AbstractLamsTestCase
{
    //---------------------------------------------------------------------
    // Dependent services
    //---------------------------------------------------------------------
    private IMonitoringService monitoringService;
    private IUserManagementService usermanageService;
    private ILessonDAO lessonDao; 
    private IActivityDAO activityDao;
    //---------------------------------------------------------------------
    // Testing Data - Constants
    //---------------------------------------------------------------------
    private final Integer TEST_USER_ID = new Integer(1);
    private final Integer TEST_LEARNER_ID = new Integer(2);
    private final Integer TEST_STAFF_ID = new Integer(3);
    private final long TEST_LEARNING_DESIGN_ID = 1;
    private final long TEST_COPIED_LEARNING_DESIGN_ID = 2;
    private final Integer TEST_ORGANIZATION_ID = new Integer(1);
    private final Long TEST_SCHEDULE_GATE_ID = new Long(27);
    
    //it might be different because it is automatically generated by database
    //TODO create a get lesson by design method in lesson dao.
    private static Long TEST_LESSON_ID = null;
    //---------------------------------------------------------------------
    // Testing Data - Instance Variables
    //---------------------------------------------------------------------
    private User testUser;
    private User testStaff;
    private User testLearner;
    private Organisation testOrganisation;
    /*
     * @see AbstractLamsCommonTestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        monitoringService = (IMonitoringService)this.context.getBean("monitoringService");
        usermanageService = (IUserManagementService)this.context.getBean("userManagementService");
        lessonDao = (LessonDAO)this.context.getBean("lessonDAO");
        activityDao = (ActivityDAO)this.context.getBean("activityDAO");
        
        initializeTestingData();
    }
    /**
     * @see AbstractLamsCommonTestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Constructor for TestMonitoringService.
     * @param name
     */
    public TestMonitoringService(String name)
    {
        super(name);
    }
    
    /**
     * @see org.lamsfoundation.lams.AbstractLamsTestCase#getContextConfigLocation()
     */
    protected String[] getContextConfigLocation()
    {
        return new String[] { "/org/lamsfoundation/lams/tool/toolApplicationContext.xml",
  			  				  "/org/lamsfoundation/lams/monitoring/monitoringApplicationContext.xml",
  			  				  "/org/lamsfoundation/lams/lesson/lessonApplicationContext.xml",
  			  				  "/org/lamsfoundation/lams/learningdesign/learningDesignApplicationContext.xml",
  			  				  "/WEB-INF/authoringApplicationContext.xml",
  			  				  "/org/lamsfoundation/lams/tool/survey/dataAccessContext.xml",
  			  				  "/org/lamsfoundation/lams/tool/survey/surveyApplicationContext.xml",        					  
				  			  "applicationContext.xml"};    
    }
    /**
     * @see org.lamsfoundation.lams.AbstractLamsTestCase#getHibernateSessionFactoryName()
     */
    protected String getHibernateSessionFactoryName()
    {
        return "coreSessionFactory";
    }
    public void testInitializeLesson()
    {
        
        Lesson testLesson = monitoringService.initializeLesson("Test_Lesson",
                                                               "Test_Description",
                                                               TEST_LEARNING_DESIGN_ID,
                                                               testUser);
        TEST_LESSON_ID=testLesson.getLessonId();
        Lesson createdLesson = lessonDao.getLesson(TEST_LESSON_ID);
        assertNotNull(createdLesson);
        assertEquals("verify the design",TEST_COPIED_LEARNING_DESIGN_ID,createdLesson.getLearningDesign().getLearningDesignId().longValue());
        assertEquals("verify the user", TEST_USER_ID,createdLesson.getUser().getUserId());
        assertEquals("verify the lesson state",Lesson.CREATED,createdLesson.getLessonStateId());
        
        
        //createdLesson.getLessonClass().getGroups().clear();
        //lessonDao.deleteLesson(createdLesson);
    }

    public void testCreateLessonClassForLesson()
    {
        LinkedList learners = new LinkedList();
        learners.add(testLearner);
        LinkedList staffs = new LinkedList();
        staffs.add(testStaff);
        
        Lesson testLesson = monitoringService.createLessonClassForLesson(TEST_LESSON_ID.longValue(),
                                                                         testOrganisation,
                                                                         learners,
                                                                         staffs);
        Lesson createdLesson = lessonDao.getLesson(TEST_LESSON_ID);
        
        assertEquals("verify the staff group",staffs.size(),createdLesson.getLessonClass().getStaffGroup().getUsers().size());
        assertEquals("verify the organization",TEST_ORGANIZATION_ID,createdLesson.getOrganisation().getOrganisationId());
        assertEquals("verify number of the learners",1,createdLesson.getAllLearners().size());
        assertEquals("verify the lesson class",Grouping.CLASS_GROUPING_TYPE,createdLesson.getLessonClass().getGroupingTypeId());
        assertEquals("verify the learner group",1,createdLesson.getLessonClass().getGroups().size());

        
    }
    
    public void testStartlesson() throws LamsToolServiceException
    {
        monitoringService.startlesson(TEST_LESSON_ID.longValue());
        assertTrue(true);
        
        Lesson startedLesson = lessonDao.getLesson(TEST_LESSON_ID);
        
        assertNotNull(startedLesson);
        assertEquals("verify the lesson status",Lesson.STARTED_STATE,startedLesson.getLessonStateId());
        
        
    }

    public void testOpenGate()
    {
        monitoringService.openGate(TEST_SCHEDULE_GATE_ID);
        Activity openedScheduleGate = activityDao.getActivityByActivityId(TEST_SCHEDULE_GATE_ID);
        assertTrue("the gate should be opened",((GateActivity)openedScheduleGate).getGateOpen().booleanValue());
    }

    public void testCloseGate()
    {
        monitoringService.closeGate(TEST_SCHEDULE_GATE_ID);
        Activity closedScheduleGate = activityDao.getActivityByActivityId(TEST_SCHEDULE_GATE_ID);
        assertTrue("the gate should be closed",!((GateActivity)closedScheduleGate).getGateOpen().booleanValue());
    }
    
    public void testForceCompleteLessonByUser()
    {
    }

    /**
     * Initialize all instance variables for testing
     */
    private void initializeTestingData()
    {
        testUser = usermanageService.getUserById(TEST_USER_ID);
        testStaff = usermanageService.getUserById(TEST_STAFF_ID);
        testLearner = usermanageService.getUserById(TEST_LEARNER_ID);        
        testOrganisation = usermanageService.getOrganisationById(TEST_ORGANIZATION_ID);
    }
    
    
    public void testGetAllLessons()throws IOException{
    	String packet = monitoringService.getAllLessons();
    	System.out.print(packet);
    }
    public void testGetLessonDetails() throws IOException{
    	String packet = monitoringService.getLessonDetails(new Long(1));
    	System.out.print(packet);
    }
    public void testGetLessonLearners() throws IOException{
    	String packet = monitoringService.getLessonLearners(new Long(1));
    	System.out.println(packet);
    }
    public void testGetLessonDesign()throws IOException{
    	String packet = monitoringService.getLearningDesignDetails(new Long(1));
    	System.out.println(packet);
    }
    public void testGetAllLearnersProgress() throws IOException{
    	String packet = monitoringService.getAllLearnersProgress(new Long(1));
    	System.out.println(packet);
    }
    public void testGetLearnerActivityURL() throws Exception{
    	String packet = monitoringService.getLearnerActivityURL(new Long(26),new Integer(2));
    	System.out.println(packet);
    }
    public void  testGellAllContributeActivities()throws IOException{
    	String packet = monitoringService.getAllContributeActivities(new Long(1));
    	System.out.println(packet);
    }
    public void testMoveLesson() throws IOException{
    	String packet = monitoringService.moveLesson(new Long(1),new Integer(3),new Integer(1));
    	System.out.println(packet);
    }
    public void testRenameLesson()throws IOException{
    	String packet = monitoringService.renameLesson(new Long(1),"New name after renaming",new Integer(1));
    	System.out.println(packet);
    }
    
    
}
