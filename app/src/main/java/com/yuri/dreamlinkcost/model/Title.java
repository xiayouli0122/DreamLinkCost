package com.yuri.dreamlinkcost.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Yuri on 2015/7/18.
 */
@Table(name = "title")
public class Title extends Model {

    @Column(name = "title")
    public String mTitle;
}
