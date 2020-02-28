package ru.mail.polis.homework.simple;

import java.util.function.ToDoubleFunction;

public class HomeworkTask {

    /**
     * Нужно численно посчитать интеграл от a до b с шагом delta от функции function
     * Для того, что бы получить значение по Y для точки X, надо просто написать function.applyAsDouble(t)
     * Считаем, что функция определена на всем пространстве от a до b
     */
    public static double calcIntegral(double a, double b, ToDoubleFunction<Double> function, double delta) {
        double integral = 0D;
        while (a < b) {
            integral += function.applyAsDouble(a);
            a += delta;
        }
        return integral * delta;
    }

    /**
     * Вывести номер максимальной цифры. Счет начинается слева направо,
     * выводим номер первой максимальной цифры (если их несколько)
     */
    public static byte maxNumber(long a) {
        a = Math.abs(a);
        long max = 0;
        byte numberOfMax = 1;
        while (a != 0) {
            max = Math.max(max, a % 10);
            numberOfMax = max == a % 10 ? 1 : ++numberOfMax;
            a /= 10;
        }
        return numberOfMax;
    }


    /**
     * Даны две точки в пространстве (x1, y1) и (x2, y2). Вам нужно найти Y координату третьей точки (x3, y3),
     * которая находится на той же прямой что и первые две.
     */
    public static double lineFunction(int x1, int y1, int x2, int y2, int x3) {
        return (double) (x3 - x1) * (y2 - y1) / (x2 - x1) + y1;
    }

    /**
     * Даны 4 точки в пространстве A(x1, y1), B(x2, y2), C(x3, y3), D(x4, y4). Найдите площадь выпуклого
     * четырехуголька ABCD.
     * Это дополнительное задание, необязательное для выполнения
     */
    
    public static double square(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        return (Math.abs(x1 * y2 + x2 * y3 + x3 * y1 - x2 * y1 - x3 * y2 - x1 * y3)
                + Math.abs(x4 * y2 + x2 * y3 + x3 * y4 - x2 * y4 - x3 * y2 - x4 * y3)
                + Math.abs(x4 * y1 + x1 * y3 + x3 * y4 - x1 * y4 - x3 * y1 - x4 * y3)
                + Math.abs(x4 * y1 + x1 * y2 + x2 * y4 - x1 * y4 - x2 * y1 - x4 * y2))
                / 4.0;
        /*return Math.abs((x1 + x2) * (y1 - y2) + (x2 + x3) * (y2 - y3)
                + (x3 + x4) * (y3 - y4) + (x4 + x1) * (y4 - y1))
                / 2.0;*/
    }
    /**
     * Вариант выычисления площади сразу для четырёхугольника более ёмкий,
     * но результат зависит от порядка передачи точек в метод (Оба варианта тесты прошли)
     */
}
