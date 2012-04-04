package com.linvald.emacs.data;
import java.io.Serializable;
import java.util.ArrayList;


public class EmacsSubject implements Serializable {
	private static final long serialVersionUID = 7526472295622776147L;
	private ArrayList<Command> data = new ArrayList<Command>();
	private String subjectName; 
	private String category;
	
	public EmacsSubject(String subjectName){
		this.subjectName = subjectName;
	}
	
	public String getSubjectName(){
		return this.subjectName;
	}
	
	public void addKeyMapping(Command mapping){
		data.add(mapping);
	}
	
	public ArrayList<Command> getKeyMappings(){
		return this.data;
	}
	
	@Override
	public String toString(){
		return this.subjectName;
	}
	
	public String getCategory(){
		return category;
	}
	
	public void setCategory(String category){
		this.category = category;
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof EmacsSubject){
			return ((EmacsSubject) other).getSubjectName().equals(getSubjectName());
		}
		return false;
	}
	
	public int hashCode(){
		int hash = 7;
		hash = 31 * hash + (null == getSubjectName() ? 0 : getSubjectName().hashCode());
		return hash;
	}
}
