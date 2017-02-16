package com.bean;

public class User {
    private String name;

    private String passwd;

    public String getName() {
        return name;
    }
    @Override
	public String toString() {
		return "User [name=" + name + ", passwd=" + passwd + "]";
	}
	public User(String name,String passwd) {
        this. name=name;
        this.passwd=passwd;
    }

    public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd == null ? null : passwd.trim();
    }
}