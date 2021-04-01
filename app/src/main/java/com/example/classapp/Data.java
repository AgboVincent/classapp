package com.example.classapp;

public class Data {
    String name, regno,email,password,cascore,examscore,grade,image;

    public Data(){

    }




    public Data(String name, String regno, String email, String password, String cascore, String examscore, String grade, String image){

        this.name=name;
        this.regno=regno;
        this.email=email;
        this.password=password;
        this.cascore=cascore;
        this.examscore=examscore;
        this.grade=grade;
        this.image=image;

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCascore() {
        return cascore;
    }

    public void setCascore(String cascore) {
        this.cascore = cascore;
    }

    public String getExamscore() {
        return examscore;
    }

    public void setExamscore(String examscore) {
        this.examscore = examscore;
    }
    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
class Common{
    public static Data currentUser;


}
