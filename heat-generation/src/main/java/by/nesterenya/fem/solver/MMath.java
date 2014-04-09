package by.nesterenya.fem.solver;

/// <summary>
/// Статический класс для работы с матрицами
/// </summary>
public class MMath
{
    /// <summary>
    /// Транспонирование матрицы
    /// </summary>
    /// <param name="matr"></param>
    /// <returns></returns>
    static public double[][] T(double[][] matr)//
    {
        double[][] tMatr = new double[matr[0].length][matr.length];
        for (int i = 0; i < matr.length; i++)
            for (int j = 0; j < matr[0].length; j++)
                tMatr[j][i] = matr[i][j];
        return tMatr;
    }

    /// <summary>
    /// Суммирование матриц
    /// </summary>
    /// <param name="matrA"></param>
    /// <param name="matrB"></param>
    /// <returns></returns>
    static public double[][] SUM(double[][] matrA, double[][] matrB)
    {
        double[][] mulMatr = new double[matrA.length][ matrB[0].length];
        for (int i = 0; i < matrA.length; i++)
            for (int j = 0; j < matrA[0].length; j++)
                mulMatr[i][j] = matrA[i][ j] + matrB[i][ j];
        return mulMatr;
    }

    /// <summary>
    /// Умножение матрицы на матрицу
    /// </summary>
    /// <param name="matrA"></param>
    /// <param name="matrB"></param>
    /// <returns></returns>
    static public double[][] MUL(double[][] matrA, double[][] matrB)
    {
        double[][] mulMatr = new double[matrA.length][ matrB[0].length];

        for (int i = 0; i < matrA.length; i++)
            for (int j = 0; j < matrB[0].length; j++)
                for (int k = 0; k < matrB.length; k++)
                    mulMatr[i][ j] += matrA[i][ k] * matrB[k][ j];
        return mulMatr;
    }

    /// <summary>
    /// Отразить верхнюю диогональную матрицу
    /// </summary>
    /// <param name="matrA"></param>
    static public void REF_UP(double[][] matrA)
    {
        for(int i = 0;i<matrA.length-1;i++)
            for (int j = i+1; j < matrA[0].length; j++)
            {
                matrA[j][ i] = matrA[i][ j];
            }
    }

    /// <summary>
    /// Умножение матрицы на вектор
    /// </summary>
    /// <param name="matrA"></param>
    /// <param name="vectorB"></param>
    /// <returns></returns>
    static public double[] MUL(double[][] matrA, double[] vectorB)
    {
        double[] mulMatr = new double[matrA.length];

        for (int i = 0; i < matrA.length; i++)
            for (int k = 0; k < vectorB.length; k++)
                mulMatr[i] += matrA[i][ k] * vectorB[k];
        return mulMatr;
    }

    /// <summary>
    /// Умножение матрицы на число
    /// </summary>
    /// <param name="matrA"></param>
    /// <param name="Zn"></param>
    /// <returns></returns>
    static public double[][] MUL(double[][] matrA, double Zn)
    {
        double[][] mulMatr = new double[matrA.length][ matrA[0].length];

        for (int i = 0; i < matrA.length; i++)
            for (int k = 0; k < matrA[0].length; k++)
                mulMatr[i][ k] = matrA[i][ k] * Zn;
        return mulMatr;
    }

    /// <summary>
    /// Решение системы уравнения методом Гауса
    /// </summary>
    /// <param name="A"></param>
    /// <param name="B"></param>
    /// <returns></returns>
    static public double[] gausSLAU(double[][] A, double[] B) throws Exception
    {
        double[] X = new double[A.length];

        int ln = A.length;

        for (int i = 0; i < ln; i++) //выполнение прямого хода Метода Гауса
        {
            if (Math.abs(A[i][i]) < 0.000000001) //проверка стоит ли на главной диогонали 0
            {
                double maxEl = 0;  //максимальный найденый элемент ниже нулевого
                int ind = 0; //индекс строки с которой нужно произвести перестановку
                for (int j = i; j < ln; j++) //если был найден 0
                {
                    if (Math.abs(A[j][i]) > maxEl) //выполняем поиско подходящей строки
                    {
                        maxEl = Math.abs(A[j][i]);
                        ind = j;
                    }
                }
                if (ind != 0)
                {
                    double tmp = 0;
                    for (int j = 0; j < ln; j++)  //перестановка строк
                    {
                        tmp = A[i][ j];
                        A[i][ j] = A[ind][ j];
                        A[ind][ j] = tmp;
                    }
                    tmp = B[i];
                    B[i] = B[ind];
                    B[ind] = tmp;
                }
            }

            /*получение треугольной матрицы*/
            for (int j = i + 1; j < ln; j++)
            {
                if (Math.abs(A[j][ i]) > 0.0000000001)
                {
                    double lokKaf = A[j][ i] / A[i][ i];  //определение коэффициента домножения
                    for (int k = 0; k < ln; k++) //отнимание строк
                    {
                        A[j][ k] = A[j][ k] - A[i][ k] * lokKaf;
                    }
                    B[j] = B[j] - B[i] * lokKaf;
                }
            }
        }
        /*-------обратный ход метода Гауса---------*/
     

            double det = 1;
            /*for (int i = 0; i < n; i++)
            {
                det *= A[i, i];
            }*/
            if (Math.abs(A[ln - 1][ ln - 1]) < 0.000000001)
            {
                if (Math.abs(B[ln - 1]) < 0.000000001)
                {
                    throw new Exception("Система имеет множество решений");
                }
                else
                    if (Math.abs(det) < 0.000000001)
                        throw new Exception("Система не имеет решений, определитель равен 0");
            }

            X = new double[ln]; //инициализация массива ответов
            for (int i = ln - 1; i >= 0; i--)
            {
                double h = B[i];// h присваиваем ответ из массива сил
                for (int j = 0; j < ln; j++)
                    h = h - X[j] * A[i][ j]; //вычетание элементов строки матрицы
                X[i] = h / A[i][ i];//h  делим на коэффициент при иксу
            }
      

        return X;
    }

    /// <summary>
    /// Решение системы уравнения методом Гауса НЕ ИЗМЕНЯЕТ ВХОДЯЩИЕ МАТРИЦУ И ВЕКТОР
    /// </summary>
    /// <param name="A"></param>
    /// <param name="B"></param>
    /// <returns></returns>
    static public double[] gausSLAU_NCH(double[][] inA, double[] inB) throws Exception
    {
        double[][] A; //= new double[inA.length][ inA[0].length];
        double[] B;//= new double[inB.length];

       
        
        //Дублирование матриц
//        for (int i = 0; i < A.length; i++)
//        {
//            for (int j = 0; j < A[0].length; j++)
//                A[i][ j] = inA[i][ j];
//            B[i] = inB[i];
//        }        
        A = inA.clone();
        for (int i = 0; i < inA.length; i++) {
            A[i] = inA[i].clone();
        }
        B = inB.clone();

        double[] X = new double[A.length];

        int ln = A.length;

        for (int i = 0; i < ln; i++) //выполнение прямого хода Метода Гауса
        {
            if (Math.abs(A[i][ i]) < 0.000000001) //проверка стоит ли на главной диогонали 0
            {
                double maxEl = 0;  //максимальный найденый элемент ниже нулевого
                int ind = 0; //индекс строки с которой нужно произвести перестановку
                for (int j = i; j < ln; j++) //если был найден 0
                {
                    if (Math.abs(A[j][ i]) > maxEl) //выполняем поиско подходящей строки
                    {
                        maxEl = Math.abs(A[j][ i]);
                        ind = j;
                    }
                }
                if (ind != 0)
                {
                    double tmp = 0;
                    for (int j = 0; j < ln; j++)  //перестановка строк
                    {
                        tmp = A[i][ j];
                        A[i][ j] = A[ind][ j];
                        A[ind][ j] = tmp;
                    }
                    tmp = B[i];
                    B[i] = B[ind];
                    B[ind] = tmp;
                }
            }

            /*получение треугольной матрицы*/
            for (int j = i + 1; j < ln; j++)
            {
                if (Math.abs(A[j][ i]) > 0.0000000001)
                {
                    double lokKaf = A[j][i] / A[i][ i];  //определение коэффициента домножения
                    for (int k = 0; k < ln; k++) //отнимание строк
                    {
                        A[j][ k] = A[j][ k] - A[i][ k] * lokKaf;
                    }
                    B[j] = B[j] - B[i] * lokKaf;
                }
            }
        }
        /*-------обратный ход метода Гауса---------*/


            double det = 1;
            /*for (int i = 0; i < n; i++)
            {
                det *= A[i, i];
            }*/
            if (Math.abs(A[ln - 1][ ln - 1]) < 0.000000001)
            {
                if (Math.abs(B[ln - 1]) < 0.000000001)
                {
                    throw new Exception("Система имеет множество решений");
                }
                else
                    if (Math.abs(det) < 0.000000001)
                        throw new Exception("Система не имеет решений, определитель равен 0");
            }

            X = new double[ln]; //инициализация массива ответов
            for (int i = ln - 1; i >= 0; i--)
            {
                double h = B[i];// h присваиваем ответ из массива сил
                for (int j = 0; j < ln; j++)
                    h = h - X[j] * A[i][ j]; //вычетание элементов строки матрицы
                X[i] = h / A[i][ i];//h  делим на коэффициент при иксу
            }
      

        return X;
    }

    /// <summary>
    /// Получение обратной матрицы.
    /// </summary>
    /// <param name="A"></param>
    /// <returns></returns>
    static public double[][] INV(double[][] A) throws Exception
    {
        double[][] B = new double[A.length][ A[0].length];
        for (int i = 0; i < A.length; i++)
            B[i][ i] = 1;
      
            for (int i = 0; i < A.length; i++) //выполнять цыкл пока не будет обработана вся матрица
            {
                if ((Math.abs(A[i][ i]) < 0.00001)) //проверка стоит ли на главной диогонали 0
                {
                    double maxEl = 0;
                    int ind = 0;
                    for (int j = i; j < A.length; j++) //если был найден 0
                    {
                        if (Math.abs(A[j][ i]) > maxEl) //выполняем поиско подходящей строки
                        {
                            maxEl = Math.abs(A[j][i]);
                            ind = j;
                        }
                    }
                    if (ind != 0)
                    {
                        for (int j = 0; j < A.length; j++)  //перестановка строк
                        {
                            double tmp = 0;
                            tmp = A[i][ j];
                            A[i][ j] = A[ind][ j];
                            A[ind][ j] = tmp;
                            tmp = B[i][ j];
                            B[i][ j] = B[ind][j];
                            B[ind][ j] = tmp;
                        }
                    }
                }
                double kaf = A[i][ i];  //преобразование элемента стоящего на главной доогонали в 1
                if (Math.abs(kaf) < 0.0000000000000001)
                {
                    throw new Exception("Нельзя получить обратную матрицу");
                }

                for (int j = 0; j < A.length; j++)
                {
                    A[i][j] = A[i][ j] / kaf;
                    B[i][ j] = B[i][ j] / kaf;
                }
                /*получение единичной матрицы*/
                for (int j = 0; j < A.length; j++)
                {
                    if (i != j && Math.abs(A[j][ i]) > 0.000000001)
                    {
                        double lokKaf = A[j][ i] / A[i][ i];
                        for (int k = 0; k < A.length; k++)
                        {
                            A[j][ k] = A[j][ k] - A[i][ k] * lokKaf;
                            B[j][k] = B[j][ k] - B[i][ k] * lokKaf;
                        }
                    }
                }
            }
        
        return B;
    }

    /// <summary>
    /// Нахождение определителя матрицы.
    /// </summary>
    /// <param name="A"></param>
    /// <returns></returns>
    static public double DET(double[][] A)
    {


        int ln = A.length;

        for (int i = 0; i < ln; i++) //выполнение прямого хода Метода Гауса
        {
            if (Math.abs(A[i][i]) < 0.000000001) //проверка стоит ли на главной диогонали 0
            {
                double maxEl = 0;  //максимальный найденый элемент ниже нулевого
                int ind = 0; //индекс строки с которой нужно произвести перестановку
                for (int j = i; j < ln; j++) //если был найден 0
                {
                    if (Math.abs(A[j][i]) > maxEl) //выполняем поиско подходящей строки
                    {
                        maxEl = Math.abs(A[j][ i]);
                        ind = j;
                    }
                }
                if (ind != 0)
                {
                    double tmp = 0;
                    for (int j = 0; j < ln; j++)  //перестановка строк
                    {
                        tmp = A[i][j];
                        A[i][ j] = A[ind][ j];
                        A[ind][j] = tmp;
                    }

                }
            }

            /*получение треугольной матрицы*/
            for (int j = i + 1; j < ln; j++)
            {
                if (Math.abs(A[j][ i]) > 0.0000000001)
                {
                    double lokKaf = A[j][ i] / A[i][ i];  //определение коэффициента домножения
                    for (int k = 0; k < ln; k++) //отнимание строк
                    {
                        A[j][ k] = A[j][ k] - A[i][ k] * lokKaf;
                    }

                }
            }
        }

        double X = 1;

        for (int i = 0; i < ln; i++)
            X *= A[i][ i];

        return X;
    }

    public static double[] MUL(double[] vector, double cnst) {
      double[] result = new double[vector.length];
      for(int i =0;i<vector.length;i++) {
        result[i] = vector[i] * cnst;
      }
      return result;
    }
}