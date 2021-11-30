package form;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import model.NhanVien;
import util.XFile;

/**
 *
 * @author Trung Hieu
 */
public class Assignment extends javax.swing.JFrame implements Runnable {

    private ArrayList<NhanVien> nvLst = new ArrayList<>();
    private int selectedRow = -1;
    private final String FILE_NAME = "File_import.txt";
    private final String PAGINATION_LABEL = "Record: @page of @size";
    private boolean isNew = true;
    private final String EMAIL_REGEX = "\\w+\\@\\w+(\\.\\w+){1,2}";
    private DecimalFormat df = new DecimalFormat("#");

    public Assignment() {
        initComponents();
        setLocationRelativeTo(null);

        Thread clockThread = new Thread(this);
        clockThread.start();

        this.lblEmailError.setVisible(false);
        this.lblHoTenError.setVisible(false);
        this.lblLuongError.setVisible(false);
        this.lblMaNVError.setVisible(false);
        this.lblTuoiError.setVisible(false);

        this.btnFirst.setEnabled(false);
        this.btnPrevious.setEnabled(false);
        this.btnNext.setEnabled(false);
        this.btnLast.setEnabled(false);

        this.initPaginationLabel();
    }

    @Override
    public void run() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        while (true) {
            this.lblClock.setText(sdf.format(new Date()));
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void clearForm() {
        this.txtMaNV.setText(null);
        this.txtHoTen.setText(null);
        this.txtTuoi.setText(null);
        this.txtEmail.setText(null);
        this.txtLuong.setText(null);
        this.selectedRow = -1;
        this.initPaginationLabel();
        this.isNew = true;
        this.btnFirst.setEnabled(false);
        this.btnPrevious.setEnabled(false);
        this.btnNext.setEnabled(false);
        this.btnLast.setEnabled(false);
    }

    private void fillToTable() {
        DefaultTableModel dtm = (DefaultTableModel) this.tblNV.getModel();
        dtm.setRowCount(0);
        for (NhanVien nv : this.nvLst) {
            Object[] rowData = new Object[]{
                nv.getMaNV(),
                nv.getHoTen(),
                nv.getTuoi(),
                nv.getEmail(),
                df.format(nv.getLuong())
            };
            dtm.addRow(rowData);
        }
    }

    private void initPaginationLabel() {
        String label = this.PAGINATION_LABEL.replace("@page", String.valueOf(this.selectedRow + 1))
                .replace("@size", String.valueOf(this.nvLst.size()));
        this.lblPagination.setText(label);
    }

    private NhanVien validateAndGetValue() {
        boolean validate = true;
        NhanVien nv = new NhanVien();
        String maNV = this.txtMaNV.getText();
        String hoTen = this.txtHoTen.getText();
        String tuoi = this.txtTuoi.getText();
        String email = this.txtEmail.getText();
        String luong = this.txtLuong.getText();

        if (maNV.isEmpty()) {
            this.txtMaNV.setBorder(new LineBorder(Color.red, 1));
            this.lblMaNVError.setVisible(true);
            this.lblMaNVError.setText("Không được để trống Mã nhân viên");
            validate = false;
        } else {
            ArrayList<String> maNVLst = new ArrayList<>();
            for (NhanVien nhanVien : this.nvLst) {
                maNVLst.add(nhanVien.getMaNV());
            }
            if (maNVLst.contains(maNV) && this.isNew) {
                this.txtMaNV.setBorder(new LineBorder(Color.red, 1));
                this.lblMaNVError.setVisible(true);
                this.lblMaNVError.setText("Đã tồn tại mã nhân viên: " + maNV);
                validate = false;
            } else {
                this.txtMaNV.setBorder(new JTextField().getBorder());
                this.lblMaNVError.setVisible(false);
                nv.setMaNV(maNV);
            }
        }

        if (hoTen.isEmpty()) {
            this.txtHoTen.setBorder(new LineBorder(Color.red, 1));
            this.lblHoTenError.setVisible(true);
            this.lblHoTenError.setText("Không được để trống Họ tên");
            validate = false;
        } else {
            this.txtHoTen.setBorder(new JTextField().getBorder());
            this.lblHoTenError.setVisible(false);
            nv.setHoTen(hoTen);
        }

        if (tuoi.isEmpty()) {
            this.txtTuoi.setBorder(new LineBorder(Color.red, 1));
            this.lblTuoiError.setVisible(true);
            this.lblTuoiError.setText("Không được để trống Tuổi");
            validate = false;
        } else {
            try {
                if (Integer.parseInt(tuoi) >= 16 && Integer.parseInt(tuoi) <= 55) {
                    nv.setTuoi(Integer.parseInt(tuoi));
                    this.txtTuoi.setBorder(new JTextField().getBorder());
                    this.lblTuoiError.setVisible(false);
                } else {
                    this.txtTuoi.setBorder(new LineBorder(Color.red, 1));
                    this.lblTuoiError.setVisible(true);
                    this.lblTuoiError.setText("Tuổi phải từ 16 đến 55");
                    validate = false;
                }
            } catch (Exception e) {
                this.txtTuoi.setBorder(new LineBorder(Color.red, 1));
                this.lblTuoiError.setVisible(true);
                this.lblTuoiError.setText("Tuổi phải là số");
                validate = false;
            }
        }

        if (email.isEmpty()) {
            this.txtEmail.setBorder(new LineBorder(Color.red, 1));
            this.lblEmailError.setVisible(true);
            this.lblEmailError.setText("Không được để trống Email");
            validate = false;
        } else {
            Pattern pattern = Pattern.compile(this.EMAIL_REGEX);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                this.txtEmail.setBorder(new LineBorder(Color.red, 1));
                this.lblEmailError.setVisible(true);
                this.lblEmailError.setText("Email không đúng định dạng");
                validate = false;
            } else {
                nv.setEmail(email);
                this.txtEmail.setBorder(new JTextField().getBorder());
                this.lblEmailError.setVisible(false);
            }
        }

        if (luong.isEmpty()) {
            this.txtLuong.setBorder(new LineBorder(Color.red, 1));
            this.lblLuongError.setVisible(true);
            this.lblLuongError.setText("Không được để trống Lương");
            validate = false;
        } else {
            try {
                if (Double.parseDouble(luong) > 5000000) {
                    nv.setLuong(Double.parseDouble(luong));
                    this.txtLuong.setBorder(new JTextField().getBorder());
                    this.lblLuongError.setVisible(false);
                } else {
                    this.txtLuong.setBorder(new LineBorder(Color.red, 1));
                    this.lblLuongError.setVisible(true);
                    this.lblLuongError.setText("Lương phải trên 5 triệu");
                    validate = false;
                }
            } catch (Exception e) {
                this.txtLuong.setBorder(new LineBorder(Color.red, 1));
                this.lblLuongError.setVisible(true);
                this.lblLuongError.setText("Lương phải là số");
                validate = false;
            }
        }

        if (!validate) {
            return null;
        } else {
            return nv;
        }
    }

    private void fillToForm(NhanVien nv) {
        this.txtHoTen.setText(nv.getHoTen());
        this.txtMaNV.setText(nv.getMaNV());
        this.txtLuong.setText(String.valueOf(df.format(nv.getLuong())));
        this.txtTuoi.setText(String.valueOf(nv.getTuoi()));
        this.txtEmail.setText(nv.getEmail());
        this.isNew = false;
        this.initPaginationLabel();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtMaNV = new javax.swing.JTextField();
        txtHoTen = new javax.swing.JTextField();
        txtTuoi = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtLuong = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnFind = new javax.swing.JButton();
        btnOpen = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        btnFirst = new javax.swing.JButton();
        btnPrevious = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        lblPagination = new javax.swing.JLabel();
        lblClock = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblNV = new javax.swing.JTable();
        lblMaNVError = new javax.swing.JLabel();
        lblHoTenError = new javax.swing.JLabel();
        lblTuoiError = new javax.swing.JLabel();
        lblEmailError = new javax.swing.JLabel();
        lblLuongError = new javax.swing.JLabel();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("MÃ NHÂN VIÊN");

        jLabel2.setText("HỌ VÀ TÊN");

        jLabel3.setText("TUỔI");

        jLabel4.setText("EMAIL");

        jLabel5.setText("LƯƠNG");

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        btnNew.setText("NEW");
        btnNew.setToolTipText("");
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });

        btnSave.setText("SAVE");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnDelete.setText("DELETE");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnFind.setText("FIND");
        btnFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindActionPerformed(evt);
            }
        });

        btnOpen.setText("OPEN");
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });

        btnExit.setText("EXIT");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnNew, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                    .addComponent(btnFind, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnOpen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnExit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnNew)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFind)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOpen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExit)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnFirst.setText("|<");
        btnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstActionPerformed(evt);
            }
        });

        btnPrevious.setText("<<");
        btnPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousActionPerformed(evt);
            }
        });

        btnNext.setText(">>");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        btnLast.setText(">|");
        btnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });

        lblPagination.setForeground(new java.awt.Color(255, 0, 0));
        lblPagination.setText("Record: 0 of 0");

        lblClock.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblClock.setForeground(new java.awt.Color(255, 0, 0));
        lblClock.setText("00:00 AM");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setText("QUẢN LÝ NHÂN VIÊN");

        tblNV.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "MÃ", "HỌ TÊN", "TUỔI", "EMAIL", "LƯƠNG"
            }
        ));
        tblNV.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblNVMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblNV);

        lblMaNVError.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblMaNVError.setForeground(new java.awt.Color(255, 0, 0));
        lblMaNVError.setText("Error");

        lblHoTenError.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblHoTenError.setForeground(new java.awt.Color(255, 0, 0));
        lblHoTenError.setText("Error");

        lblTuoiError.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblTuoiError.setForeground(new java.awt.Color(255, 0, 0));
        lblTuoiError.setText("Error");

        lblEmailError.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblEmailError.setForeground(new java.awt.Color(255, 0, 0));
        lblEmailError.setText("Error");

        lblLuongError.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblLuongError.setForeground(new java.awt.Color(255, 0, 0));
        lblLuongError.setText("Error");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(182, 182, 182)
                        .addComponent(jLabel8)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtMaNV, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                                .addComponent(jLabel2))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtTuoi, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblMaNVError)
                                    .addComponent(lblTuoiError)
                                    .addComponent(lblEmailError)
                                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnFirst)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnPrevious)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnNext)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnLast)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPagination)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(lblHoTenError)
                                        .addComponent(txtHoTen, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                        .addComponent(txtLuong))
                                    .addComponent(lblLuongError))
                                .addGap(85, 85, 85)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblClock)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(35, 35, 35))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblClock)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtMaNV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(txtHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMaNVError)
                            .addComponent(lblHoTenError))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTuoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(txtLuong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTuoiError)
                            .addComponent(lblLuongError))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblEmailError)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnFirst)
                            .addComponent(btnPrevious)
                            .addComponent(btnNext)
                            .addComponent(btnLast)
                            .addComponent(lblPagination))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        this.clearForm();
    }//GEN-LAST:event_btnNewActionPerformed

    private void tblNVMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblNVMouseClicked
        this.lblEmailError.setVisible(false);
        this.lblHoTenError.setVisible(false);
        this.lblLuongError.setVisible(false);
        this.lblMaNVError.setVisible(false);
        this.lblTuoiError.setVisible(false);
        this.txtMaNV.setBorder(new JTextField().getBorder());
        this.txtHoTen.setBorder(new JTextField().getBorder());
        this.txtLuong.setBorder(new JTextField().getBorder());
        this.txtTuoi.setBorder(new JTextField().getBorder());
        this.txtEmail.setBorder(new JTextField().getBorder());
        
        this.selectedRow = this.tblNV.getSelectedRow();

        if (this.selectedRow == -1) {
            return;
        }

        NhanVien nv = new NhanVien();
        nv.setMaNV(this.tblNV.getValueAt(this.selectedRow, 0).toString());
        nv.setHoTen(this.tblNV.getValueAt(this.selectedRow, 1).toString());
        nv.setTuoi(Integer.parseInt(this.tblNV.getValueAt(this.selectedRow, 2).toString()));
        nv.setEmail(this.tblNV.getValueAt(this.selectedRow, 3).toString());
        nv.setLuong(Double.parseDouble(this.tblNV.getValueAt(this.selectedRow, 4).toString()));
        this.fillToForm(nv);
        this.initPaginationLabel();
        this.isNew = false;
        if (this.selectedRow == 0) {
            this.btnFirst.setEnabled(false);
            this.btnPrevious.setEnabled(false);
        } else {
            this.btnFirst.setEnabled(true);
            this.btnPrevious.setEnabled(true);
        }
        if (this.selectedRow == (this.nvLst.size() - 1)) {
            this.btnLast.setEnabled(false);
            this.btnNext.setEnabled(false);
        } else {
            this.btnLast.setEnabled(true);
            this.btnNext.setEnabled(true);
        }
    }//GEN-LAST:event_tblNVMouseClicked

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        XFile.writeObject(FILE_NAME, this.nvLst);
        System.exit(0);
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        this.lblEmailError.setVisible(false);
        this.lblHoTenError.setVisible(false);
        this.lblLuongError.setVisible(false);
        this.lblMaNVError.setVisible(false);
        this.lblTuoiError.setVisible(false);
        this.txtMaNV.setBorder(new JTextField().getBorder());
        this.txtHoTen.setBorder(new JTextField().getBorder());
        this.txtLuong.setBorder(new JTextField().getBorder());
        this.txtTuoi.setBorder(new JTextField().getBorder());
        this.txtEmail.setBorder(new JTextField().getBorder());
        
        this.nvLst = (ArrayList<NhanVien>) XFile.readObject(FILE_NAME);
        if (!this.nvLst.isEmpty()) {
            NhanVien nv = this.nvLst.get(0);
            this.fillToForm(nv);
            this.selectedRow = 0;
        }
        this.fillToTable();
        this.initPaginationLabel();
        this.btnFirst.setEnabled(false);
        this.btnPrevious.setEnabled(false);
        this.btnNext.setEnabled(true);
        this.btnLast.setEnabled(true);
    }//GEN-LAST:event_btnOpenActionPerformed

    private void btnFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindActionPerformed
        String maNV = this.txtMaNV.getText();
        NhanVien result = new NhanVien();
        boolean find = false;
        if (maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không được để trống mã nhân viên");
            return;
        }

        for (NhanVien nv : this.nvLst) {
            if (nv.getMaNV().equals(maNV)) {
                find = true;
                result = nv;
                break;
            }
        }

        if (!find) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên có mã: " + maNV);
            return;
        }

        this.fillToForm(result);
        this.selectedRow = this.nvLst.indexOf(result);
        this.initPaginationLabel();
        if (this.selectedRow == 0) {
            this.btnFirst.setEnabled(false);
            this.btnPrevious.setEnabled(false);
        } else {
            this.btnFirst.setEnabled(true);
            this.btnPrevious.setEnabled(true);
        }
        if (this.selectedRow == (this.nvLst.size() - 1)) {
            this.btnLast.setEnabled(false);
            this.btnNext.setEnabled(false);
        } else {
            this.btnLast.setEnabled(true);
            this.btnNext.setEnabled(true);
        }
    }//GEN-LAST:event_btnFindActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        NhanVien nv = this.validateAndGetValue();
        if (nv == null) {
            return;
        }
        if (isNew) {
            this.nvLst.add(nv);
        } else {
            this.nvLst.set(this.selectedRow, nv);
        }
        this.fillToTable();
        this.clearForm();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        String maNV = this.txtMaNV.getText();
        NhanVien result = new NhanVien();
        boolean find = false;
        if (maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không được để trống mã nhân viên");
            return;
        }

        for (NhanVien nv : this.nvLst) {
            if (nv.getMaNV().equals(maNV)) {
                find = true;
                result = nv;
                break;
            }
        }

        if (!find) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên có mã: " + maNV);
            return;
        }

        this.nvLst.remove(result);
        this.fillToTable();
        this.clearForm();
        JOptionPane.showMessageDialog(this, "Xóa thành công");
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        if (!this.nvLst.isEmpty()) {
            NhanVien nv = this.nvLst.get(0);
            this.selectedRow = 0;
            this.fillToForm(nv);
            this.btnFirst.setEnabled(false);
            this.btnPrevious.setEnabled(false);
            this.btnNext.setEnabled(true);
            this.btnLast.setEnabled(true);
        }
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousActionPerformed
        if (!this.nvLst.isEmpty() && this.selectedRow > 0) {
            NhanVien nv = this.nvLst.get(this.selectedRow - 1);
            this.selectedRow = this.selectedRow - 1;
            this.fillToForm(nv);
            this.btnNext.setEnabled(true);
            this.btnLast.setEnabled(true);
            if (this.selectedRow == 0) {
                this.btnFirst.setEnabled(false);
                this.btnPrevious.setEnabled(false);
            } else {
                this.btnFirst.setEnabled(true);
                this.btnPrevious.setEnabled(true);
            }
        }
    }//GEN-LAST:event_btnPreviousActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        if (!this.nvLst.isEmpty() && this.selectedRow < (this.nvLst.size() - 1)) {
            NhanVien nv = this.nvLst.get(this.selectedRow + 1);
            this.selectedRow = this.selectedRow + 1;
            this.fillToForm(nv);
            this.btnPrevious.setEnabled(true);
            this.btnFirst.setEnabled(true);
            if (this.selectedRow == (this.nvLst.size() - 1)) {
                this.btnLast.setEnabled(false);
                this.btnNext.setEnabled(false);
            } else {
                this.btnLast.setEnabled(true);
                this.btnNext.setEnabled(true);
            }
        }
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        if (!this.nvLst.isEmpty()) {
            NhanVien nv = this.nvLst.get(this.nvLst.size() - 1);
            this.selectedRow = this.nvLst.size() - 1;
            this.fillToForm(nv);
            this.btnFirst.setEnabled(true);
            this.btnPrevious.setEnabled(true);
            this.btnNext.setEnabled(false);
            this.btnLast.setEnabled(false);
        }
    }//GEN-LAST:event_btnLastActionPerformed

    public static void main(String args[]) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Assignment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Assignment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Assignment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Assignment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Assignment().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnFind;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnPrevious;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblClock;
    private javax.swing.JLabel lblEmailError;
    private javax.swing.JLabel lblHoTenError;
    private javax.swing.JLabel lblLuongError;
    private javax.swing.JLabel lblMaNVError;
    private javax.swing.JLabel lblPagination;
    private javax.swing.JLabel lblTuoiError;
    private javax.swing.JTable tblNV;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtHoTen;
    private javax.swing.JTextField txtLuong;
    private javax.swing.JTextField txtMaNV;
    private javax.swing.JTextField txtTuoi;
    // End of variables declaration//GEN-END:variables
}
