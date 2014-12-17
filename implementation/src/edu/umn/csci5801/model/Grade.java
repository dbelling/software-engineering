/**
 * Grade
 */

package edu.umn.csci5801.model;

public enum Grade {
    A(4.000f),
    B(3.000f),
    C(2.000f),
    D(1.000f),
    F(0.0f),
    S(2.0f), 
    N(0.0f),
    _(0.0f);
    
    private final float gpa;
    
    Grade(float val) {
        this.gpa = val;
    }
    
    public float getGpa() {
        return gpa;
    }
}
