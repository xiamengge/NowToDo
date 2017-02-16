package db;

public class UserState {
String name;
int state;
	/**
	 * @param args
	 */
public UserState(String name,int state){
   this.name=name;
   this.state=state;
}
public String getName() {
         return name;
     }
    public void setName(String name) {
        this.name = name;
    }
     public int getState() {
        return state;
     }
    public void setState(int state) {
        this.state =state;
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

