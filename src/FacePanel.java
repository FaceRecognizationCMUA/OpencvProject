/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.Graphics;
import java.awt.image.BufferedImage;  
import java.io.ByteArrayInputStream;  
import java.io.IOException;  
import javax.imageio.ImageIO;  
import javax.swing.*;  
import org.opencv.core.Mat;  
import org.opencv.core.MatOfByte;  
import org.opencv.highgui.Highgui;  
/**
 *
 * @author Sky Xu <Sky Xu at Carnegie Mellon University>
 */
public class FacePanel extends javax.swing.JPanel {
    private static final long serialVersionUID = 1L;  
    private BufferedImage image;  
    /**
     * Creates new form FacePanel1
     */
    public FacePanel() {
        initComponents();
    }
     public boolean matToBufferedImage(Mat matrix) {  
          MatOfByte mb=new MatOfByte();  
          Highgui.imencode(".jpg", matrix, mb);
          try {  
                this.image = ImageIO.read(new ByteArrayInputStream(mb.toArray())); 
          } catch (IOException e) {  
               e.printStackTrace();  
               return false; // Error  
          }  
       return true; // Successful  
     }  
     public void paintComponent(Graphics g){  
          super.paintComponent(g);   
          if (this.image==null) return;         
           g.drawImage(this.image,10,10,this.image.getWidth(),this.image.getHeight(), null);
     }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        shootBtn = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        shootBtn.setText("Shoot");
        shootBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shootBtnActionPerformed(evt);
            }
        });

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(294, Short.MAX_VALUE)
                .addComponent(shootBtn)
                .addGap(166, 166, 166))
            .addGroup(layout.createSequentialGroup()
                .addGap(138, 138, 138)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
<<<<<<< HEAD
                .addContainerGap(235, Short.MAX_VALUE)
=======
                .addContainerGap(189, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
>>>>>>> 86ab5d0ae08b1e681a025d9655809918c81d7283
                .addComponent(shootBtn)
                .addGap(34, 34, 34))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void shootBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shootBtnActionPerformed
        // TODO add your handling code here:
        M.flag=false;
        System.out.println("ahahah");
    }//GEN-LAST:event_shootBtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        
         M.flag=false;
        System.out.println("ahahah");
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton shootBtn;
    // End of variables declaration//GEN-END:variables
}
