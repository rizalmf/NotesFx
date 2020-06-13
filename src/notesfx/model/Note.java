/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notesfx.model;

/**
 *
 * @author PKane_NS
 */
public class Note {
    private int id;
    private String title;
    private String text;
    private String url_picture;
    private String color_bg;
    private String color_txt;
    private String is_picture;
    private int x;
    private int y;

    public String getUrl_picture() {
        return url_picture;
    }

    public void setUrl_picture(String url_picture) {
        this.url_picture = url_picture;
    }

    public String getIs_picture() {
        return is_picture;
    }

    public void setIs_picture(String is_picture) {
        this.is_picture = is_picture;
    }

    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getColor_bg() {
        return color_bg;
    }

    public void setColor_bg(String color_bg) {
        this.color_bg = color_bg;
    }

    public String getColor_txt() {
        return color_txt;
    }

    public void setColor_txt(String color_txt) {
        this.color_txt = color_txt;
    }
    
    
}
