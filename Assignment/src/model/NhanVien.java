package model;

import java.io.Serializable;

/**
 *
 * @author Trung Hieu
 */
public class NhanVien implements Serializable {
    private String maNV;
    
    private String hoTen;
    
    private int tuoi;
    
    private String email;
    
    private double luong;

    public NhanVien() {
    }

    public NhanVien(String maNV, String hoTen, int tuoi, String email, double luong) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.tuoi = tuoi;
        this.email = email;
        this.luong = luong;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public int getTuoi() {
        return tuoi;
    }

    public void setTuoi(int tuoi) {
        this.tuoi = tuoi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getLuong() {
        return luong;
    }

    public void setLuong(double luong) {
        this.luong = luong;
    }
}
