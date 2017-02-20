package com.app.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.app.model.Admin;
import com.app.model.Course;
import com.app.model.Employee;
import com.app.model.Person;
import com.app.model.Student;
import com.app.model.Subject;
import com.app.model.Transaction;
import com.app.model.User;
import com.app.model.Year;

public class DBManager {
	
	private DatabaseConnection dbconn;
	private  Connection conn;
	private static DBManager manager;
	
	public static final DBManager getInstance(){
		if(manager == null)
			manager = new DBManager();
		return manager;
		
	}
	public final void setDatabaseConnection(DatabaseConnection connection){
		dbconn = connection;
	}
	
	public final void openConnection(){
		conn = dbconn.getConnection();
	}
	
	public final void closeConnection(){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public final DatabaseConnection getDatabaseConnection(){
		return dbconn;
	}
	
	public User getUserWithUsernameAndPassword(String username,String password){
		try{
			User user = null;
			for(User isThisUser : getListOfUser()){
				if(username.equals(isThisUser.getUsername()) && password.equals(isThisUser.getPassword())){
					user = isThisUser;
				}
			}
			return user;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public List<User> getListOfUser(){
		try{
			List<User> listOfUser = new ArrayList<User>();
			String sql = "SELECT * FROM tbluser";
			PreparedStatement pst = (PreparedStatement) conn.prepareStatement(sql);
			ResultSet result = pst.executeQuery();
			while(result.next()){
				User user = null;
				String id = result.getString("user_id");
				String username = result.getString("username");
				String password = result.getString("password");
				String accessType = result.getString("access_type");
				boolean isActivate = (result.getInt("is_activated") == 1);
				
				if(accessType.equals("ADMIN")){
					
					user = new Admin();
					user.setUsername(username);
					user.setPassword(password);
					user.setAccessType(accessType);
					user.setActivate(isActivate);
			
				}else if(accessType.equals("STUDENT")){
					
					user = new Student();
					user.setUsername(username);
					user.setPassword(password);
					user.setAccessType(accessType);
					user.setActivate(isActivate);
					
				}else if(accessType.equals("EMPLOYEE")){
					user = new Employee(0,null);
					user.setUsername(username);
					user.setPassword(password);
					user.setAccessType(accessType);
					user.setActivate(isActivate);
				}
				
				user.setId(id);
				listOfUser.add(user);
			}
			return listOfUser;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public void updateUserPasswordByWithUsername(String username ,String newPassword){
		try{
			String sql="UPDATE tbluser SET password = ? WHERE username = ?;";
			PreparedStatement pst = (PreparedStatement) conn.prepareStatement(sql);
			pst.setString(1, newPassword);
			pst.setString(2, username);
			pst.executeUpdate();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public List<Course> getListOfCourse(){
		try{
			List<Course> listOfCourse = new ArrayList<Course>();
			String sql = "SELECT * FROM tblcourse";
			PreparedStatement pst = conn.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()){
				
				String courseId = rs.getString("course_id");
				String courseCode = rs.getString("course_code");
				String courseDefinition = rs.getString("course_definition");
				List<Year> listOfYear = getListOfYearByCourseId(courseId);
				Course course = new Course(courseId , 
						courseCode , courseDefinition , listOfYear);
				
				listOfCourse.add(course);
			}
			
			return listOfCourse;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public List<Year> getListOfYearByCourseId(String courseId){
		try{
			List<Year> listOfYear = new ArrayList<Year>();
			String code = (courseId != null ? courseId : "");
			String sql = "SELECT * FROM tblyear where course_id = ?";

			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, code);
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()){
				Year year = new Year(rs.getString("year_id") , rs.getString("year_code"));
				listOfYear.add(year);
			}
			
			return listOfYear;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public synchronized void savePerson(Person person , String personId, String studentNumber, String employeeNumber){
		try{
			String sql = "INSERT INTO tblperson(person_id , last_name , first_name,"
					+ "middle_name , student_info, employee_info) VALUES(? , ? , ? ,?, ?, ?)";
			if(studentNumber.matches("0[0-9a-zA-Z]+"))
				studentNumber.replace("0", "");
			
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, personId);
			pst.setString(2, person.getLastName());
			pst.setString(3, person.getFirstName());
			pst.setString(4, person.getMiddleName());
			pst.setString(5, studentNumber);
			pst.setString(6, employeeNumber);
			
			pst.executeUpdate();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public synchronized void savePerson(Person person , String personId, String studentNumber, String employeeNumber, boolean isAuto){
		try{
			
			if(isAuto){
				String sql = "INSERT INTO tblperson(last_name , first_name,"
						+ "middle_name , student_info, employee_info) VALUES(? , ? ,?, ?, ?)";
				
				PreparedStatement pst = conn.prepareStatement(sql);
				
				pst.setString(1, person.getLastName());
				pst.setString(2, person.getFirstName());
				pst.setString(3, person.getMiddleName());
				pst.setString(4, studentNumber);
				pst.setString(5, employeeNumber);
			
			pst.executeUpdate();
			
			}else
				savePerson(person, personId, studentNumber, employeeNumber);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

	public synchronized void saveUser(User user, String userId,  String personId){
		try{
			String sql = "INSERT INTO tbluser(user_id , username , password,"
					+ "access_type , is_activated, personal_info) VALUES(?,?,?,?,?,?)";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, userId);
			pst.setString(2, user.getUsername());
			pst.setString(3, user.getPassword());
			pst.setString(4, user.getAccessType());
			pst.setInt(5, (user.isActivate() ? 1 : 0));
			pst.setString(6, personId);
			
			pst.executeUpdate();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public synchronized void saveUser(User user, String userId,  String personId, boolean isAuto){
		try{
			if(isAuto){
				String sql = "INSERT INTO tbluser(username , password,"
						+ "access_type , is_activated, personal_info) VALUES(?,?,?,?,?)";
				PreparedStatement pst = conn.prepareStatement(sql);

				pst.setString(1, user.getUsername());
				pst.setString(2, user.getPassword());
				pst.setString(3, user.getAccessType());
				pst.setInt(4, (user.isActivate() ? 1 : 0));
				pst.setString(5, personId);
				
				pst.executeUpdate();
			}else
				saveUser(user, userId, personId);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public synchronized void saveStudent(Student student){
		try{
			String sql = "INSERT INTO tblstudent(student_number , email , course_id,"
					+ "year_id) VALUES(? , ? , ? ,?)";
			
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setLong(1, student.getStudentNumber());
			pst.setString(2, student.getEmail());
			pst.setString(3,student.getCourse());
			pst.setString(4, student.getYear());
			
			pst.executeUpdate();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public synchronized void saveEmployee(Employee employee){
		try{
			String sql = "INSERT INTO tbl_employee(employee_number , email) VALUES(? , ?)";
			
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setLong(1, employee.getEmployeeNumber());
			pst.setString(2, employee.getEmail());
			pst.executeUpdate();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getTrackPassword(String prefix){
		try{
			
			String id = null;
			String sql = "SELECT * FROM tbltrack_password LIMIT 1";
			
			PreparedStatement pst = conn.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()){
				if(prefix.equalsIgnoreCase("UID"))
					id = rs.getString("user_id");
				else if(prefix.equalsIgnoreCase("PID"))
					id = rs.getString("person_id");
			}
			return id;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public Course getCourseByCode(String code){
		Course course = null;
		for(Course c : getListOfCourse()){
			if(c.getCourseCode().equalsIgnoreCase(code))
				course = c;
		}
		return course;
	}
	
	public Course getCourseById(String id){
		Course course = null;
		for(Course c : getListOfCourse()){
			if(c.getCourseId().equalsIgnoreCase(id))
				course = c;
		}
		return course;
	}
	
	public Year getYearByCode(String code , String courseId){
		Year year = null;
		for(Year y : getListOfYearByCourseId(courseId)){
			if(y.getYearCode().equalsIgnoreCase(code))
				year = y;
		}
		return year;
	}
	
	public Year getYearById(String id , String courseId){
		Year year = null;
		for(Year y : getListOfYearByCourseId(courseId)){
			if(y.getYearId().equalsIgnoreCase(id))
				year = y;
		}
		return year;
	}
	
	@Deprecated
	public List<Subject> getAllSubjectByYearAndSem(int year, int semester){
		try{
			List<Subject> listOfSubject = new ArrayList<Subject>();
			String sql = "SELECT * FROM tbl_subject WHERE year = ? AND semester = ?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, year);
			pst.setInt(2, semester);
			
			ResultSet result = pst.executeQuery();
			
			while(result.next()){
				Subject subject = new Subject();
				subject.setCategory(result.getString("category"));
				subject.setCourseId(result.getString("course_id"));
				subject.setDescription(result.getString("subject_desc"));
				subject.setId(result.getString("subject_id"));
				subject.setName(result.getString("subject_name"));
				subject.setPrerequisite(result.getString("prerequisite"));
				subject.setSemester(result.getInt("semester"));
				subject.setUnit(result.getInt("unit"));
				subject.setYear(result.getInt("year"));
				listOfSubject.add(subject);
			}
			return listOfSubject;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Subject> getAllSubjectByYearSemeterAndCourseId(int year, int semester, String courseId){
		try{
			List<Subject> listOfSubject = new ArrayList<Subject>();
			String sql = "SELECT * FROM tbl_subject WHERE year = ? AND semester = ? AND course_id = ?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, year);
			pst.setInt(2, semester);
			pst.setString(3, courseId);
			
			ResultSet result = pst.executeQuery();
			
			while(result.next()){
				Subject subject = new Subject();
				subject.setCategory(result.getString("category"));
				subject.setCourseId(result.getString("course_id"));
				subject.setDescription(result.getString("subject_desc"));
				subject.setId(result.getString("subject_id"));
				subject.setName(result.getString("subject_name"));
				subject.setPrerequisite(result.getString("prerequisite"));
				subject.setSemester(result.getInt("semester"));
				subject.setUnit(result.getInt("unit"));
				subject.setYear(result.getInt("year"));
				listOfSubject.add(subject);
			}
			return listOfSubject;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public void addTransaction(Transaction transaction){
		try{
			String sql = "INSERT INTO tbl_transaction(student_number, subject_id, status) VALUES(?,?,?)";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1,(int) transaction.getUserId());
			pst.setString(2, transaction.getSubjectId());
			pst.setString(3, transaction.getStatus());
			pst.executeUpdate();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public int getStudentNumberByPerson(String personId){
		try{
			String id = "";
			String sql = "SELECT student_info FROM tblperson WHERE person_id = ? LIMIT 1";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, personId);
			
			ResultSet rs = pst.executeQuery();
			if(rs.next()){
				id = rs.getString("student_info");
			}
			
			return Integer.parseInt(id);
			
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}
	
	public String getPersonIdByUser(String userId){
		try{
			String id ="";
			String sql = "SELECT personal_info FROM tbluser WHERE user_id = ? LIMIT 1";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, userId);
			
			ResultSet rs = pst.executeQuery();
			if(rs.next()){
				id = rs.getString("personal_info");
			}
			
			return id;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public Student getStudentById(int id){
		try{
			Student student = null;
			String sql = "SELECT * FROM tblstudent WHERE student_number = ? LIMIT 1";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, id);
			
			ResultSet rs = pst.executeQuery();
			if(rs.next()){
				student = new Student();
				student.setEmail(rs.getString("email"));
				student.setStudentNumber(rs.getInt("student_number"));
				student.setCourse(rs.getString("course_id"));
				student.setYear(rs.getString("year_id"));
			}
			
			return student;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Transaction> getAllTransactionBySubjectId(String subjectId){
		try{
			List<Transaction> list = new ArrayList<Transaction>();
			String sql= "SELECT * FROM tbl_transaction WHERE subject_id = ?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, subjectId);
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				Transaction t = new Transaction();
				t.setSubjectId(rs.getString("subject_id"));
				t.setUserId(rs.getInt("student_number"));
				t.setStatus(rs.getString("status"));
				list.add(t);
			}
			return list;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public Person getPersonByStudentInfo(String id){
		try{
			Person person = null;
			String sql = "SELECT * FROM tblperson WHERE student_info = ? LIMIT 1";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, id);
			
			ResultSet rs = pst.executeQuery();
			if(rs.next()){
				person = new Student();
				person.setFirstName(rs.getString("first_name"));
				person.setMiddleName(rs.getString("last_name"));
				person.setLastName(rs.getString("middle_name"));
			}
			
			return person;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
