package com.fs.peruappsocial.Models;

public class Post {

private String postKey;
    private String titulo;
    private String descripcion;
    private String picture;
    private String userid;
    private String userfoto;


    public Post(String titulo, String descripcion, String picture, String userid, String userfoto) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.picture = picture;
        this.userid = userid;
        this.userfoto = userfoto;

    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserfoto() {
        return userfoto;
    }

    public void setUserfoto(String userfoto) {
        this.userfoto = userfoto;
    }


}
