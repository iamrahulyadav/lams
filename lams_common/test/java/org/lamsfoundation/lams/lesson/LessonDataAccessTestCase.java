/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0 
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
/* $$Id$$ */
package org.lamsfoundation.lams.lesson;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.hibernate.HibernateException;
import org.lamsfoundation.lams.learningdesign.Group;
import org.lamsfoundation.lams.learningdesign.Grouping;
import org.lamsfoundation.lams.learningdesign.LearningDesign;
import org.lamsfoundation.lams.learningdesign.dao.ILearningDesignDAO;
import org.lamsfoundation.lams.learningdesign.dao.hibernate.LearningDesignDAO;
import org.lamsfoundation.lams.lesson.dao.ILearnerProgressDAO;
import org.lamsfoundation.lams.lesson.dao.ILessonClassDAO;
import org.lamsfoundation.lams.lesson.dao.ILessonDAO;
import org.lamsfoundation.lams.lesson.dao.hibernate.LearnerProgressDAO;
import org.lamsfoundation.lams.lesson.dao.hibernate.LessonClassDAO;
import org.lamsfoundation.lams.lesson.dao.hibernate.LessonDAO;
import org.lamsfoundation.lams.test.AbstractCommonTestCase;
import org.lamsfoundation.lams.usermanagement.Organisation;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.dao.IBaseDAO;
import org.lamsfoundation.lams.dao.hibernate.BaseDAO;


/**
 * The super class for lesson related data access test. It defines services, 
 * such as initialize lesson data and clean up lesson data, for its descendents.
 * It also act as a good example of creating and deleting lesson object that
 * can be used by client.
 * @author Jacky Fang 2/02/2005
 * 
 */
public class LessonDataAccessTestCase extends AbstractCommonTestCase
{

    //---------------------------------------------------------------------
    // DAO instances for initializing data
    //---------------------------------------------------------------------
    protected IBaseDAO baseDao;
    protected ILearningDesignDAO learningDesignDao;
    protected ILessonDAO lessonDao;
    protected ILessonClassDAO lessonClassDao;
    protected ILearnerProgressDAO learnerProgressDao;
    
    //---------------------------------------------------------------------
    // Domain Object instances
    //---------------------------------------------------------------------
    protected Lesson testLesson;
    protected User testUser;
    protected LearningDesign testLearningDesign;
    protected Organisation testOrg;
    protected LessonClass testLessonClass;
    protected LearnerProgress testLearnerProgress;
    
    //---------------------------------------------------------------------
    // Class level constants
    //---------------------------------------------------------------------
    private final Integer TEST_USER_ID = new Integer(1);
    private final Long TEST_LEARNING_DESIGN_ID = new Long(1);
    private final Integer TEST_ORGANIZATION_ID = new Integer(1);
    private final int TEST_GROUP_ORDER_ID = 0;
    private final String TEST_GROUP_NAME_STAFF = "staffgroup";
    private final String TEST_GROUP_NAME_LEARNER = "learnergroup";


    //---------------------------------------------------------------------
    // Overidden methods
    //---------------------------------------------------------------------
    /**
     * @param name
     */
    public LessonDataAccessTestCase(String name)
    {
        super(name);
    }

    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        baseDao = (BaseDAO) this.context.getBean("userDAO");
        learningDesignDao = (LearningDesignDAO) this.context.getBean("learningDesignDAO");
 
        //retrieve test domain data
        testUser = (User)baseDao.find(User.class,TEST_USER_ID);
        testLearningDesign = learningDesignDao.getLearningDesignById(TEST_LEARNING_DESIGN_ID);
        testOrg = (Organisation)baseDao.find(Organisation.class,TEST_ORGANIZATION_ID);
   
        //get lesson related daos
        lessonDao = (LessonDAO)this.context.getBean("lessonDAO");
        lessonClassDao = (LessonClassDAO)this.context.getBean("lessonClassDAO");
        learnerProgressDao = (LearnerProgressDAO)this.context.getBean("learnerProgressDAO");
    }

    /**
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();

    }
    protected String[] getContextConfigLocation() {
		return new String[] {"org/lamsfoundation/lams/localApplicationContext.xml",
							 "/org/lamsfoundation/lams/lesson/lessonApplicationContext.xml"};
	}

    

    //---------------------------------------------------------------------
    // Data initialization and finalization methods
    //---------------------------------------------------------------------
    /**
     * Initialize the whole lesson object. This also act as an example of 
     * creating a new lesson for a learning design.
     * 
     * We have to creat lesson class before create group due to the not null
     * field(grouping_id) in the group object.
     * @throws HibernateException
     * 
     */
    protected void initializeTestLesson() throws HibernateException
    {
        this.initLessonClassData();
        lessonClassDao.saveLessonClass(this.testLessonClass);

        this.setUpGroupsForClass();
        lessonClassDao.updateLessonClass(this.testLessonClass);

        this.initLessonData();
        lessonDao.saveLesson(testLesson);
        
        super.getSession().flush();
    }
    
    /**
     * Remove the target lesson from database. We have to clean up the groups 
     * first. We are using hibernate transparent persistant mechanism. To make
     * the deletion of groups work, we have to setup "all-delete-orphan" for
     * groups collection
     * @param lesson the lesson needs to be removed.
     */
    protected void cleanUpLesson(Lesson lesson)
    {
        lessonDao.deleteLesson(lesson);
    }
    
    protected void cleanUpTestLesson() throws HibernateException
    {
        //super.initializeHibernateSession();
        //super.getSession().lock(testLesson,LockMode.READ);
        this.cleanUpLesson(testLesson);
        
        //super.finalizeHibernateSession();
    }
    /**
     * Create a lesson class with empty group information.
     */
    protected void initLessonClassData()
    {
        //make a copy of lazily initialized activities
        Set activities = new HashSet(testLearningDesign.getActivities());
        testLessonClass = new LessonClass(null, //grouping id
                                          new HashSet(),//groups
                                          activities,
                                          null, //staff group 
                                          testLesson);
    }
    
    /**
     * Setup groups(learner group and staff group) fro created lesson class.
     */
    protected void setUpGroupsForClass()
    {
        //create a new staff group
        Set staffs = new HashSet();
        staffs.add(testUser);
        Group staffGroup = new Group(null,//group id 
        							 TEST_GROUP_NAME_STAFF,
                                     TEST_GROUP_ORDER_ID,
                                     null,
                                     testLessonClass,
                                     staffs,
                                     new HashSet(),
                                     null);
        testLessonClass.setStaffGroup(staffGroup);

        //create learner class group
        Set learnergroups = new HashSet();
        //make a copy of lazily initialized users
        learnergroups.add(testUser);
        Group learnerClassGroup = new Group(null,//group id
        									TEST_GROUP_NAME_LEARNER,
                                            testLessonClass.getNextGroupOrderIdCheckName(TEST_GROUP_NAME_LEARNER),
                                            null,
                                            testLessonClass,
                                            learnergroups,
                                            new HashSet(),
                                            null);//tool session, should be empty now

        learnergroups.add(learnerClassGroup);
        learnergroups.add(staffGroup);
        testLessonClass.setGroups(learnergroups);

    }
    
    /**
     * Create lesson based on the information we initialized.
     */
    protected void initLessonData() {
	testLesson = new Lesson("Test Lesson", "Test lesson description", new Date(System.currentTimeMillis()),
		testUser, Lesson.CREATED, null, testLearningDesign, new HashSet(), false, false, true, false, false,
		false, false, null);
	testLesson.setLessonClass(testLessonClass);
	testLesson.setOrganisation(testOrg);
    }
  

    /**
     * This init method is called when we need to learner progress testing.
     *
     */
    protected void initLearnerProgressData()
    {
        //pre-condition
        if(testLesson == null)
            throw new IllegalArgumentException("Can't initialize progress without " +
            		"lesson");
        testLearnerProgress = new LearnerProgress(testUser,testLesson);
    }
    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------
    /**
     * Helper method to validate the created lesson class. This validation
     * method can be reused by sub-classes.
     * @param lessonClass 
     */
    protected void assertLessonClass(LessonClass lessonClass)
    {
        assertEquals("check up number of activities",11,lessonClass.getActivities().size());
        assertEquals("check up staff groups",1,lessonClass.getStaffGroup().getUsers().size());
        assertEquals("check up grouping types, should be class grouping",Grouping.CLASS_GROUPING_TYPE,lessonClass.getGroupingTypeId());
        assertEquals("check up groups",2,lessonClass.getGroups().size());
    }
    /**
     * Helper method to validate the created lesson. This validation
     * method can be reused by sub-classes.
     * @param lesson 
     */
    protected void assertLesson(Lesson lesson)
    {
        assertEquals("check up the lesson name","Test Lesson",lesson.getLessonName());
        assertEquals("check up the lesson description","Test lesson description",lesson.getLessonDescription());
        assertEquals("check up creation time",testLesson.getCreateDateTime().toString(),
                     						  lesson.getCreateDateTime().toString());
        assertEquals("check up user who created this lesson",testUser.getLogin(),lesson.getUser().getLogin());
        assertEquals("check up the lesson state",Lesson.CREATED,lesson.getLessonStateId());
        assertEquals("check up the learning design that used to create lesson",
                     							testLearningDesign.getTitle(),
                     							lesson.getLearningDesign().getTitle());
        assertEquals("check up the organization", testOrg.getName(),lesson.getOrganisation().getName());
        assertEquals("check up the learner progresses",0,lesson.getLearnerProgresses().size());
        
    }

}