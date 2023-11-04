import java.util.*;

class Course{
    private String _cnum;
    private int _credits;
    public Course(){}
    public Course(String num, int cred){
        _cnum = num;  _credits = cred;
    }
    public void setNumber(String num){
    	_cnum = num;
    }
    public void setCredits(int cred){
    	_credits = cred;
    }
    public String getNumber(){
    	return _cnum;
    }
    public int getCredits(){
    	return _credits;
    }
    public String toString(){
    	return _cnum + " " + _credits;
    }
}
class Student{
    private String _sid;
    public Student(){}
    public Student(String d){
    	_sid = d;
    }
    public void setID(String id){
    	_sid = id;
    }
    public String getID(){
    	return _sid;
    }
    public String toString(){
    	return _sid;
    }
}
interface SearchBehavior<T, S>
{
    //T is the object S is the value
    boolean search(T obj, S v);
}
class StudentSearch implements SearchBehavior<Student, String>
{
    @Override 
    public boolean search(Student obj, String v)
    {
        return obj.getID().equals(v);
    }
}
class CourseSearch implements SearchBehavior<Course, String>
{
    @Override 
    public boolean search(Course obj, String v)
    {
        return obj.getNumber().equals(v);
    }
}
class CnumSearch implements SearchBehavior<String, String>{
	
	@Override
	public boolean search(String obj, String v) {
		return obj.equals(v);
	}
}
class AllItems<T> //T is data type variable // Generic
{
    private ArrayList<T> _items; //Generic because can hold many different things

    public AllItems(){_items = new ArrayList<T>();}
    
    public AllItems(int size) {
    	_items = new ArrayList<T>(size);
    }
    
    public void addItem(T t)
    {
        _items.add(t);
    }

    
    public <S> int findItem(S v, SearchBehavior<T, S> sb) // search  -  sb is a polymorphic reference - template and bound at runtime
    {
        for (int i = 0; i< _items.size(); i++)
        {
            if (sb.search(_items.get(i), v))
            {
                return i;
            }
        }
        return -1;
    }
    public void removeItem(int i)
    {
        if(i >= 0 && i < _items.size()) //check here so you don't have to rewrite code
            _items.remove(i);
    }

    public int size(){
    	return _items.size();
    }
    public T getItem(int i)
    {
        return _items.get(i);
    }
}
class AllStudents{
    private AllItems<Student> _students;
    public AllStudents()
    {
        _students = new AllItems<Student>();
    }
    public AllStudents(int size) {
    	_students = new AllItems<Student>(size);
    }
    public void addStudent(String id){
        _students.addItem(new Student(id));
    }
    public boolean isStudent(String id){
          int student = _students.findItem(id, new StudentSearch()); //student search has a class with a method in it
          if(student == -1) 
        	  return false;
          else
        	  return true;
    }
    public int findStudent(String id){
        return _students.findItem(id, new StudentSearch());
    }
    public void removeStudent(String id){
        int i = findStudent(id);
        _students.removeItem(i); //not written in generic

    }
    public boolean modifyStudentID(String oldId, String newId) {
    	int i = findStudent(oldId);
    	if(i < 0 )
    		return false;
    	else {
    		_students.getItem(i).setID(newId); 
    		return true;
    	}
    }
    public int size() {
    	return _students.size();
    }
    public String toString(){
        String s = "Students:\n";
        for (int i=0; i<_students.size(); i++)
            s += (_students.getItem(i).toString() + "\n");
        return s;
    }
}
class AllCourses{
    private AllItems<Course> _courses;
    public AllCourses(){
        _courses = new AllItems<Course>();
    }
    public void addCourse(String cnum, int c){
        _courses.addItem(new Course(cnum, c));
    }
    public boolean isCourse(String cnum){
        if(_courses.findItem(cnum, new CourseSearch()) == -1)
        	return false;
        else
        	return true;
    }
    public int findCourse(String cnum){
        return _courses.findItem(cnum, new CourseSearch());
    }
    public void removeCourse(String cnum){
        int i = _courses.findItem(cnum, new CourseSearch());
        _courses.removeItem(i);
    }
    public boolean modifyStudent(String oldId, String newId) {
    	int i = findCourse(oldId);
    	if(i < 0 )
    		return false;
    	else {
    		_courses.getItem(i).setNumber(newId); 
    		return true;
    	}
    }
    public int size() {
    	return _courses.size();
    }
    public String toString(){
        String s = "Courses:\n";
        for (int i=0; i<_courses.size(); i++)
            s += (_courses.getItem(i).toString() + "\n");
        return s;
    }
}
class Enrollment{
    private HashMap<String, AllItems<String>> _enroll;
    public Enrollment(){_enroll = new HashMap<String, AllItems<String>>();}
    public void addCourseToStudent(String id, String c){
        AllItems<String> t = _enroll.get(id);
        if (t == null)  // student not in enroll
            t = new AllItems<String>();
        t.addItem(c);
        _enroll.put(id, t);
    }
    public void dropStudentFromAllCourses(String id){
        if (_enroll.containsKey(id))
            _enroll.remove(id);
    }
    public boolean dropStudentFromCourse(String id, String cnum){
        // Drops a student from a course
        // If student has no more courses then remove student from hashmap
        AllItems<String> t = _enroll.get(id);
        int i = t.findItem(cnum, new CnumSearch());
        if (i == -1)
            return false;
        t.removeItem(i);
        if (t.size() == 0)
            _enroll.remove(id);
        return true;
    }
    public boolean dropCourseFromAllStudents(String cnum){
        // Drops course from all students that are enrolled in the course
        // If student has no more courses then remove student from hashmap
        // Students that need to be removed will be stored in a temporary arraylist
        // in order to remove them from hashmap after iterating
        boolean found = false;
        // list of student ids to remove from hashmap
        ArrayList<String> kt = new ArrayList<String>();
        // extract keys from hashmap to iterator through
        Set keys = _enroll.keySet();
        Iterator itr = keys.iterator();
        while (itr.hasNext()) {
            found = false;
            String k = (String)itr.next();
            AllItems<String> t = _enroll.get(k);
            int i = t.findItem(cnum, new CnumSearch());
            if (i != -1) {
                t.removeItem(i);
                if (t.size() == 0)
                    kt.add(k);
                found = true;
            }
        }
        for (int i=0; i<kt.size(); i++)
            _enroll.remove(kt.get(i));

        return found;
    }
    public String toString(){
        String s = "Enrollment:\n";
        Set keys = _enroll.keySet();
        Iterator itr = keys.iterator();
        while (itr.hasNext()) {
            String k = (String)itr.next();
            AllItems<String> t = _enroll.get(k);
            s += (k + " ");
            for (int j=0; j<t.size(); j++)
                s += (t.getItem(j) + " ");
            s += "\n";
        }

        return s;
    }

}

public class Main {
    public static void main(String[] args) {
    	Enrollment en = new Enrollment();
        en.addCourseToStudent("100", "CSC1700");
        en.addCourseToStudent("100", "CSC2150");
        en.addCourseToStudent("100", "CSC2300");
        en.addCourseToStudent("200", "CSC1700");
        en.addCourseToStudent("200", "CSC2150");
        en.addCourseToStudent("200", "CSC2300");
        en.addCourseToStudent("200", "CSC3400");
        en.addCourseToStudent("300", "CSC1700");
        en.addCourseToStudent("300", "CSC2150");
        System.out.println(en);
        en.dropStudentFromCourse("100", "CSC2150");
        System.out.println(en);
        en.dropCourseFromAllStudents("CSC1700");
        System.out.println(en);
        en.dropStudentFromAllCourses("300");
        System.out.println(en);

        AllStudents as = new AllStudents();
        AllCourses ac = new AllCourses();
        as.addStudent("100");
        as.addStudent("200");
        as.addStudent("300");
        ac.addCourse("CSC3250", 4);
        ac.addCourse("CSC1700", 4);
        ac.addCourse("MTH3270", 4);
        System.out.println(as);
        System.out.println(ac);
        System.out.println("Is Student 300: " + as.isStudent("300"));
        System.out.println("Find Student 300: " + as.findStudent("300"));
        System.out.println("Is Course CSC3250: " + ac.isCourse("CSC3250") );
        System.out.println("Find Course CSC3250: " + ac.findCourse("CSC3250"));
        as.removeStudent("300");
        System.out.println("\nAfter removing student 300");
        as.removeStudent("300");
        System.out.println(as);
        System.out.println("Is Student 300: " + as.isStudent("300"));
        System.out.println("Find Student 300: " + as.findStudent("300"));
        System.out.println("\nAfter removing course 3250");
        ac.removeCourse("CSC3250");
        System.out.println(ac);
        System.out.println("Is Course CSC3250: " + ac.isCourse("CSC3250") );
        System.out.println("Find Course CSC3250: " + ac.findCourse("CSC3250"));
        as.modifyStudentID("200", "205");
        System.out.println(as);

    }
}