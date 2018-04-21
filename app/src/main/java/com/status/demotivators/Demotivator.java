package com.status.demotivators;

public class Demotivator {

    private String urlPage;
    private String urlImage;

    public Demotivator(String urlPage, String urlImage){
        this.urlPage = urlPage;
        this.urlImage = urlImage;
    }

    public String getUrlPage(){
        return urlPage;
    }

    public String getUrlImage(){
        return urlImage;
    }

}
