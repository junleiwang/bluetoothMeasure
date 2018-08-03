package com.example.ccqzy.androidutils;

import com.example.ccqzy.QzyApplication;
import com.lidroid.xutils.util.LogUtils;

import static com.example.ccqzy.QzyApplication.Metro_ZERO;
import static com.example.ccqzy.QzyApplication.cimcs;
import static com.example.ccqzy.QzyApplication.qzyPoint;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * Created by Administrator on 2018/3/5.
 * <p>
 * 全站仪轴对准建站公式,旋转矩阵,旋转角度
 */

public class FormulaTools {

    public FormulaTools() {
    }

    /*
    * 根据水平角,垂直角,斜距,计算坐标
    * k  (1,正面  2,反面)
    * imsHzVD  全站仪返回的数值(水平角,垂直角,斜距)
    * coordinate  得到的点坐标(X,Y,Z)
    * */
    public QzyApplication.CImsPointEx coordinateFromHzVD(int k, QzyApplication.CPDObvValue imsHzVD,
                                                         QzyApplication.CImsPointEx corrdinate) {

        double dHzDist;
        if (k == 1) {
            dHzDist = cos(imsHzVD.m_dVAngle - PI / 2) * imsHzVD.m_dSlopeDist;
            corrdinate.Z = sin(PI / 2 - imsHzVD.m_dVAngle) * imsHzVD.m_dSlopeDist;
            //原来的公式
            corrdinate.Y = sin(-imsHzVD.m_dHzAngle) * dHzDist;
            //corrdinate.Y = sin(imsHzVD.m_dHzAngle) * dHzDist;
            corrdinate.X = cos(-imsHzVD.m_dHzAngle) * dHzDist;
            return corrdinate;

        } else if (k == 2) {

            //计算测站坐标系下后视点的平距和Z坐标
            if (imsHzVD.m_dVAngle > PI) {
                imsHzVD.m_dHzAngle -= PI;
                imsHzVD.m_dVAngle = 2 * PI - imsHzVD.m_dVAngle;
                if (imsHzVD.m_dHzAngle < 0)
                    imsHzVD.m_dHzAngle += 2 * PI;
            }
            dHzDist = cos(imsHzVD.m_dVAngle - PI / 2) * imsHzVD.m_dSlopeDist;
            LogUtils.d("二面cos()=" + cos(imsHzVD.m_dVAngle - PI / 2) + "////////////////" + dHzDist);
            corrdinate.Z = sin(PI / 2 - imsHzVD.m_dVAngle) * imsHzVD.m_dSlopeDist;
            LogUtils.d("sin()=" + sin(PI / 2 - imsHzVD.m_dVAngle) + "////////////////" + corrdinate.Z);
            corrdinate.Y = sin(-imsHzVD.m_dHzAngle) * dHzDist;
            // corrdinate.Y = sin(imsHzVD.m_dHzAngle) * dHzDist;
            corrdinate.X = cos(-imsHzVD.m_dHzAngle) * dHzDist;

            LogUtils.d("水平角和垂直角转成坐标" + corrdinate.X + "," + corrdinate.Y + "," + corrdinate.Z);
            return corrdinate;
        }
        return corrdinate;
    }

    /*
    * 车体坐标系的点往全站仪所在的坐标系上转
    *
    * newCS        ; 输入-点坐标
    * m_rotMatrix1 ; 输入-转换矩阵
    * meaPoint     ; 输出-点坐标
    * */
    public QzyApplication.CImsPointEx TranslatePointInv(QzyApplication.CImCS newCS, QzyApplication.CImsRotMatrix m_rotMatrix1,
                                                        QzyApplication.CImsPointEx meaPoint) {

        QzyApplication.CImsPointEx coordinate = new QzyApplication.CImsPointEx();

//     m_rotMatrix1=newCS.CalRotMatrix();

        CalRotMatrix(m_rotMatrix1, newCS);


        coordinate.X = meaPoint.X * m_rotMatrix1.A1 + meaPoint.Y * m_rotMatrix1.A2 + meaPoint.Z * m_rotMatrix1.A3;
        coordinate.Y = meaPoint.X * m_rotMatrix1.B1 + meaPoint.Y * m_rotMatrix1.B2 + meaPoint.Z * m_rotMatrix1.B3;
        coordinate.Z = meaPoint.X * m_rotMatrix1.C1 + meaPoint.Y * m_rotMatrix1.C2 + meaPoint.Z * m_rotMatrix1.C3;

        coordinate.X = coordinate.X / newCS.Scale;
        coordinate.Y = coordinate.Y / newCS.Scale;
        coordinate.Z = coordinate.Z / newCS.Scale;

        meaPoint.X = coordinate.X + newCS.X;
        meaPoint.Y = coordinate.Y + newCS.Y;
        meaPoint.Z = coordinate.Z + newCS.Z;

        LogUtils.d("测量点从车体坐标系转换到全站仪坐标系中的点坐标=" + meaPoint.X + "," + meaPoint.Y + "," + meaPoint.Z);
        return meaPoint;
    }

    /*
    * 建好站后测量的点坐标转换到车体坐标系上
    *
    * newCS        ; 输入 -- 坐标系坐标
    * m_rotMatrix1 ; 输入 -- 转换矩阵
    * meaPoint     ; 输出/输出 -- 点坐标
    * */
    public QzyApplication.CImsPointEx TranslateMeaPoint(QzyApplication.CImCS newCS, QzyApplication.CImsRotMatrix m_rotMatrix1,
                                                        QzyApplication.CImsPointEx meaPoint) {
        QzyApplication.CImsPointEx coordinate = new QzyApplication.CImsPointEx();//这个地方需要修改

//     m_rotMatrix1=newCS.CalRotMatrix();
        CalRotMatrix(m_rotMatrix1, newCS);

        meaPoint.X = meaPoint.X - newCS.X;
        meaPoint.Y = meaPoint.Y - newCS.Y;
        meaPoint.Z = meaPoint.Z - newCS.Z;

        coordinate.X = meaPoint.X * m_rotMatrix1.A1 + meaPoint.Y * m_rotMatrix1.B1 + meaPoint.Z * m_rotMatrix1.C1;
        coordinate.Y = meaPoint.X * m_rotMatrix1.A2 + meaPoint.Y * m_rotMatrix1.B2 + meaPoint.Z * m_rotMatrix1.C2;
        coordinate.Z = meaPoint.X * m_rotMatrix1.A3 + meaPoint.Y * m_rotMatrix1.B3 + meaPoint.Z * m_rotMatrix1.C3;

        meaPoint.X = newCS.Scale * coordinate.X;
        meaPoint.Y = newCS.Scale * coordinate.Y;
        meaPoint.Z = newCS.Scale * coordinate.Z;

        LogUtils.d("测量点从全站仪转换到车体坐标系中的点坐标=" + meaPoint.X + "," + meaPoint.Y + "," + meaPoint.Z);
        return meaPoint;
    }

    /*
    * 得到该点的旋转角度,全站仪去旋转
    *
    * coordinate   ; 点坐标
    * obvValue     ; 全站仪坐标系
    * */
    public boolean CalHzVFromCoordinate(QzyApplication.CImsPointEx coordinate,
                                        QzyApplication.CImsObvValue obvValue) {
        double dblSum;

        //无效观测值
        if (abs(coordinate.X) < 1e-3 || abs(coordinate.Y) < 1e-3)//===============fabs()? 1e-3?//绝对值/常量
        {
            return false;
        }

        dblSum = sqrt(pow(coordinate.X, 2) + pow(coordinate.Y, 2) + pow(coordinate.Z, 2));
        obvValue.HzAngle = -atan2(coordinate.Y, coordinate.X);//===================-atan2() ?
        obvValue.VAngle = PI / 2 - asin(coordinate.Z / dblSum);

        if (obvValue.HzAngle < 0) {
            obvValue.HzAngle = obvValue.HzAngle + 2 * PI;
        }

        LogUtils.d("全站仪的旋转角度" + obvValue.HzAngle + "///" + obvValue.VAngle);
        return true;
    }

    /*
    * 计算旋转矩阵
    *
    * rotMatrix  ; 旋转矩阵
    * rotAngle   ; 旋转角
    * */
    public void CalRotMatrix(QzyApplication.CImsRotMatrix rotMatrix, QzyApplication.CImCS rotAngle) {
//     CImsRotMatrix rotMatrix;
        rotMatrix.A1 = cos(rotAngle.RotY) * cos(rotAngle.RotZ);
        rotMatrix.A2 = -cos(rotAngle.RotY) * sin(rotAngle.RotZ);
        rotMatrix.A3 = sin(rotAngle.RotY);

        rotMatrix.B1 = cos(rotAngle.RotX) * sin(rotAngle.RotZ) + sin(rotAngle.RotX) * sin(rotAngle.RotY) * cos(rotAngle.RotZ);
        rotMatrix.B2 = cos(rotAngle.RotX) * cos(rotAngle.RotZ) - sin(rotAngle.RotX) * sin(rotAngle.RotY) * sin(rotAngle.RotZ);
        rotMatrix.B3 = -sin(rotAngle.RotX) * cos(rotAngle.RotY);

        rotMatrix.C1 = sin(rotAngle.RotX) * sin(rotAngle.RotZ) - cos(rotAngle.RotX) * sin(rotAngle.RotY) * cos(rotAngle.RotZ);
        rotMatrix.C2 = sin(rotAngle.RotX) * cos(rotAngle.RotZ) + cos(rotAngle.RotX) * sin(rotAngle.RotY) * sin(rotAngle.RotZ);
        rotMatrix.C3 = cos(rotAngle.RotX) * cos(rotAngle.RotY);

        LogUtils.d("旋转矩阵=" + rotMatrix.A1 + rotMatrix.A2 + rotMatrix.A3 + rotMatrix.B1
                + rotMatrix.B2 + rotMatrix.B3 + rotMatrix.C1 + rotMatrix.C2 + rotMatrix.C3);
    }

    /*
    * 旋转矩阵反算旋转角
    *
    * rotAngle      ; 点的旋转角
    * imsRotMatirx  ; 矩阵点
    * */
    public void CalRotAngle(QzyApplication.CImsRotAngle rotAngle, QzyApplication.CImsRotMatrix imsRotMatirx) {

        //求解旋转角
        LogUtils.d("输入的值为" + -imsRotMatirx.B3 + "//" + imsRotMatirx.C3 + "//" + imsRotMatirx.A3 + "//"
                + imsRotMatirx.A2 + "//" + imsRotMatirx.A1);
        rotAngle.RotX = atan2(-imsRotMatirx.B3, imsRotMatirx.C3);
        rotAngle.RotY = asin(imsRotMatirx.A3);// asin == ?
        //原来的
        rotAngle.RotZ = atan2(-imsRotMatirx.A2, imsRotMatirx.A1);
        //新改的
        //rotAngle.RotZ = atan2(imsRotMatirx.A1, -imsRotMatirx.A2);// atan2==?反tan()
        LogUtils.d("ceshi==" + atan2(8, 4) + "是否==1.1071487177940904");
        LogUtils.d("z=" + atan2(0.997, -0.0773));
        LogUtils.d("z=" + atan2(-0, 1));

        //非负化RotX
        if (rotAngle.RotX < 0) {
            rotAngle.RotX = rotAngle.RotX + 2 * PI;
        } else if (rotAngle.RotX > 2 * PI) {
            rotAngle.RotX = rotAngle.RotX - 2 * PI;
        }

        //非负化RotY
        if (rotAngle.RotY < 0) {
            rotAngle.RotY = rotAngle.RotY + 2 * PI;
        } else if (rotAngle.RotY > 2 * PI) {
            rotAngle.RotY = rotAngle.RotY - 2 * PI;
        }

        //非负化RotZ
        if (rotAngle.RotZ < 0) {
            rotAngle.RotZ = rotAngle.RotZ + 2 * PI;
        } else if (rotAngle.RotZ > 2 * PI) {
            rotAngle.RotZ = rotAngle.RotZ - 2 * PI;
        }

        LogUtils.d("旋转角=" + rotAngle.RotX + rotAngle.RotY + rotAngle.RotZ);
//    return rotAngle;
    }

    /*
    * 矢量反算旋转角
    *
    * rotAngle     ; 点的旋转角
    * imsRotMatrix ; 旋转矩阵点
    * vectorX      ; 点坐标
    * vectorY      ; 点坐标
    * vectorZ      ; 点坐标
    * */
    public void CalRotAngle(QzyApplication.CImsRotAngle rotAngle, QzyApplication.CImsRotMatrix imsRotMatrix,
                            QzyApplication.CImsVector vectorX, QzyApplication.CImsVector vectorY,
                            QzyApplication.CImsVector vectorZ) {
        UnitVector(vectorX);
        UnitVector(vectorY);
        UnitVector(vectorZ);

        imsRotMatrix.A1 = vectorX.I;
        imsRotMatrix.A2 = vectorY.I;
        imsRotMatrix.A3 = vectorZ.I;
        imsRotMatrix.B1 = vectorX.J;
        imsRotMatrix.B2 = vectorY.J;
        imsRotMatrix.B3 = vectorZ.J;
        imsRotMatrix.C1 = vectorX.K;
        imsRotMatrix.C2 = vectorY.K;
        imsRotMatrix.C3 = vectorZ.K;

        CalRotAngle(rotAngle, imsRotMatrix);

        LogUtils.d("旋转角中的旋转矩阵=" + imsRotMatrix.A1 + "/" + imsRotMatrix.A2 + "/"
                + imsRotMatrix.A3 + "/" + imsRotMatrix.B1 + "/" + imsRotMatrix.B2 + "/"
                + imsRotMatrix.B3 + "/" + imsRotMatrix.C1 + "/" + imsRotMatrix.C2 + "/"
                + imsRotMatrix.C3 + "xo=" + rotAngle.RotX + "////" + rotAngle.RotY + "//" + rotAngle.RotZ);
    }

    /*
    *  向量单位化
    *
    *  imsVector  ; 点坐标
    * */
    double dblSum = 0;

    public void UnitVector(QzyApplication.CImsVector imsVetor) {
        // double dblSum = 0;
        // double dblSum = 0;
        dblSum = pow(imsVetor.I, 2) + pow(imsVetor.J, 2) + pow(imsVetor.K, 2);//======================pow(变量,int)?//变量的平方
        dblSum = sqrt(dblSum);//==============================sqrt()方法名?//开平方根

        if (dblSum < Metro_ZERO) {
            return;//Metro_ZERO 常量
        }

        imsVetor.I = imsVetor.I / dblSum;
        imsVetor.J = imsVetor.J / dblSum;
        imsVetor.K = imsVetor.K / dblSum;
        LogUtils.d("点坐标" + "I=" + imsVetor.I + "J=" + imsVetor.J + "K=" + imsVetor.K + "dblsum=" + dblSum);
    }

    /*
    * 向量叉乘
    * 根据两个已知点的坐标,算出第三个点的坐标
    *
    * temVector   ;  所求点坐标
    * imsVector_1 ;  已知点坐标
    * imsVector   ;  已知点坐标
    * */
    public void CrossProduct(QzyApplication.CImsVector tempVector, QzyApplication.CImsVector imsVector_1,
                             QzyApplication.CImsVector imsVector) {
//    CImsVector tempVector;
        tempVector.I = imsVector_1.J * imsVector.K - imsVector_1.K * imsVector.J;
        tempVector.J = imsVector_1.K * imsVector.I - imsVector_1.I * imsVector.K;
        tempVector.K = imsVector_1.I * imsVector.J - imsVector_1.J * imsVector.I;

        LogUtils.d("点坐标" + "I=" + tempVector.I + "J=" + tempVector.J + "K=" + tempVector.K);
//    return tempVector;
    }

    /**
    * 返回坐标系平移量和旋转角度,x,y,z
    *
    * newCS ; 所求得的坐标系坐标,旋转角,缩放比例
    * originPoint; 原点坐标
    * axisPoint;  轴点坐标 原点坐标的z+1000
    * planePoint; 目标点坐标origint2
    * */

    public QzyApplication.CImCS CalCsParam(QzyApplication.CImCS newCS, QzyApplication.CImsPointEx originPoint,
                                           QzyApplication.CImsPointEx axisPoint, QzyApplication.CImsPointEx planePoint) {

        QzyApplication.CImsVector vectorX = new QzyApplication.CImsVector();//变量X
        QzyApplication.CImsVector vectorY = new QzyApplication.CImsVector();//变量Y
        QzyApplication.CImsVector vectorZ = new QzyApplication.CImsVector();//变量Z
        QzyApplication.CImsRotAngle rotAngle = new QzyApplication.CImsRotAngle();//转换角度
        QzyApplication.CImsRotMatrix rotMatrix = new QzyApplication.CImsRotMatrix();//转换矩阵
        // boolean bIsTrue;

        vectorZ.I = axisPoint.X - originPoint.X;//轴坐标点X - 基准坐标点X = 向量分量dx
        vectorZ.J = axisPoint.Y - originPoint.Y;//轴坐标点Y - 基准坐标点Y = 向量分量dy
        vectorZ.K = axisPoint.Z - originPoint.Z;//轴坐标点Z - 基准坐标点Z = 向量分量dz

        vectorX.I = planePoint.X - originPoint.X;//目标坐标点X - 基准坐标点X
        vectorX.J = planePoint.Y - originPoint.Y;//目标坐标点Y - 基准坐标点Y
        vectorX.K = planePoint.Z - originPoint.Z;//目标坐标点Z - 基准坐标点Z

        LogUtils.d("vZI=" + vectorZ.I + "vZJ=" + vectorZ.J + "vZK=" + vectorZ.K);
        LogUtils.d("vXI=" + vectorX.I + "vXJ=" + vectorX.J + "vZK=" + vectorX.K);

        CrossProduct(vectorY, vectorZ, vectorX);//向量差乘,利用已知两个点的坐标,算出第三个点的坐标
        CrossProduct(vectorX, vectorY, vectorZ);

        LogUtils.d("叉乘后vX=" + vectorX.I + "叉乘后vY=" + vectorX.J + "叉乘后vZ=" + vectorX.K);
        LogUtils.d("叉乘后vYI=" + vectorY.I + "叉乘后vYJ=" + vectorY.J + "叉乘后vYK=" + vectorY.K);
        CalRotAngle(rotAngle, rotMatrix, vectorX, vectorY, vectorZ);

        newCS.X = originPoint.X;
        newCS.Y = originPoint.Y;
        newCS.Z = originPoint.Z;

        newCS.RotX = rotAngle.RotX;
        newCS.RotY = rotAngle.RotY;
        newCS.RotZ = rotAngle.RotZ;

        newCS.Scale = 1.0;//比例

        LogUtils.d("坐标系" + "x=" + newCS.X + "y=" + newCS.Y + "z=" + newCS.Z + "rotx=" + newCS.RotX +
                "roty=" + newCS.RotY + "ROTz=" + newCS.RotZ + "缩放比例=" + newCS.Scale);

        return newCS;
    }

    /*
    * point1 正面坐标角度值
    * point2 翻面坐标角度值
    * point3 两面的平均角度值
    * */
    public QzyApplication.CPDObvValue averagePoint(QzyApplication.CPDObvValue point1, QzyApplication.CPDObvValue point2,
                                                   QzyApplication.CPDObvValue point3) {
        //point2转换到point1上的坐标
        point3.m_dHzAngle = point2.m_dHzAngle - PI;
        if (point3.m_dHzAngle < 0) {
            point3.m_dHzAngle = point3.m_dHzAngle + 2 * PI;
        }
        point3.m_dVAngle = 2 * PI - point2.m_dVAngle;
        point3.m_dSlopeDist = point2.m_dSlopeDist;

        LogUtils.d("将二面转到一面的值" + "x=" + point3.m_dHzAngle + "y=" + point3.m_dVAngle + "z=" + point3.m_dSlopeDist);

        point3.m_dHzAngle = (point1.m_dHzAngle + point3.m_dHzAngle) / 2;
        point3.m_dVAngle = (point1.m_dVAngle + point3.m_dVAngle) / 2;
        point3.m_dSlopeDist = (point1.m_dSlopeDist + point3.m_dSlopeDist) / 2;
        LogUtils.d("将一面,二面的平均值" + "x=" + point3.m_dHzAngle + "y=" + point3.m_dVAngle + "z=" + point3.m_dSlopeDist);
        return point3;

    }

    /*
    * 根据已知点,求全站仪的旋转角度
    * */

    // public void calculate_ts_angle(QzyApplication.CPDPoint thispoint, double t_hr, double t_vr)
    public QzyApplication.CPDObvValue calculate_ts_angle(QzyApplication.CImsPointEx thispoint, QzyApplication.CPDObvValue angleValue) {
        //赋测点信息
        double XA = thispoint.X;//测点坐标X
        double YA = thispoint.Y;//测点坐标Y
        double HA = thispoint.Z;//测点坐标H

//全站仪原点在车体坐标系下的坐标
        double XN = qzyPoint.X;
        double YN = qzyPoint.Y;
        double HN = qzyPoint.Z;

        //计算竖直旋转,除数是否为零?
        double V_Rotate_Value = 0.00;
        double HD = sqrt((XN - XA) * (XN - XA) + (YN - YA) * (YN - YA));
        if ((HA - HN) >= 0) {
            V_Rotate_Value = PI / 2 - atan((HA - HN) / HD);
            LogUtils.d(">=0//V_VALUE=" + V_Rotate_Value);
        } else if ((HA - HN) < 0) {
            V_Rotate_Value = PI / 2 + atan((HN - HA) / HD);
            LogUtils.d("<0//V_VALUE=" + V_Rotate_Value);
        }

        //计算水平旋转
        double p2_xx = (XA - XN) * cos(-cimcs.RotZ) + (YA - YN) * sin(-cimcs.RotZ);
        double p2_yy = -(XA - XN) * sin(-cimcs.RotZ) + (YA - YN) * cos(-cimcs.RotZ);

        LogUtils.d("P2_xx" + p2_xx + "//p2_yy=" + p2_yy);
        double p2_rz = atan2(p2_yy, p2_xx);
        LogUtils.d("p2rz=" + p2_rz);

        if (p2_rz < 0) {
            p2_rz += 2 * PI;
            LogUtils.d("上面的p2rz=" + p2_rz);
        }

        p2_rz = 2 * PI - p2_rz;
        LogUtils.d("下面的p2rz=" + p2_rz);


        angleValue.m_dHzAngle = p2_rz;//计算节点水平转动值
        angleValue.m_dVAngle = V_Rotate_Value;//计算节点竖直转动值

        LogUtils.d("anglevalue=" + angleValue.m_dHzAngle + "///v==" + angleValue.m_dVAngle);

        return angleValue;

    }

    /*
    * 根据三个点计算挠度
    *
    * point1  输入点
    * point2  输入点(挠度点)
    * point3  输入点
    * point4  输出点
    * */
    public double measureDeflection(QzyApplication.CImsPointEx point1, QzyApplication.CImsPointEx point2,
                                    QzyApplication.CImsPointEx point3, QzyApplication.CImsPointEx point4) {

        //定义两个向量 点外向量A,直线向量B
        QzyApplication.CImsVector A = new QzyApplication.CImsVector();
        QzyApplication.CImsVector B = new QzyApplication.CImsVector();

        //向量A是Point1指向Point2的向量
        A.I = point2.X - point1.X;
        A.J = point2.Y - point1.Y;
        A.K = point2.Z - point1.Z;

        //向量B是Point1指向Point3的向量
        B.I = point3.X - point1.X;
        B.J = point3.Y - point1.Y;
        B.K = point3.Z - point1.Z;

        //向量B单位化向量b
        UnitVector(B);

        //向量A与向量B的内积M
        double M = A.I * B.I + A.J * B.J + A.K * B.K;
        //计算Point4坐标
        point4.X = point1.X + M * B.I;
        point4.Y = point1.Y + M * B.J;
        point4.Z = point1.Z + M * B.K;

        //计算挠度值
        double result = sqrt(pow(point4.X - point2.X, 2) + pow(point4.Y - point2.Y, 2) + pow(point4.Z - point2.Z, 2));
        ;
        return result;
    }
}
