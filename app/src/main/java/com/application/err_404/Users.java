package com.application.err_404;


public class Users {

    String confirmation,name_2,name_3,name_4,name_5,count,email,name,team_name;

    public Users(){

    }

    public Users(String count, String email, String name, String name_2, String name_3, String name_4, String name_5, String team_name) {
        this.name_2 = name_2;
        this.name_3 = name_3;
        this.name_4 = name_4;
        this.name_5 = name_5;
        this.count = count;
        this.email = email;
        this.name = name;
        this.team_name = team_name;
    }


    public String getName_2() {
        return name_2;
    }

    public String getName_3() {
        return name_3;
    }

    public String getName_4() {
        return name_4;
    }

    public String getName_5() {
        return name_5;
    }

    public String getCount() {
        return count;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getTeam_name() {
        return team_name;
    }
}