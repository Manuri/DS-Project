/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dsphase2;

/**
 *
 * @author user
 */
public class Utils {
        
    public static int[] getRandomTwo(int number) {

        if (number == 2) {
            return new int[]{0, 1};
        }
        int rand1 = (int) (Math.random() * 1000 % number);
        int rand2 = (int) (Math.random() * 1000 % (number));

        while (rand1 == rand2) {
            rand2 = (int) ((Math.random() * 1000) % (number));
        }
        int[] array = {rand1, rand2};
        return array;
    }
    public static int getRandomNo(int number) {
        if (number == 0) {
            return 0;
        }
        return (int) (Math.random() * 1000 % number);
    }
    public static int getRandomNo(int number, int exception, boolean isSearcherAChild) {
        if (number == 1) {
            if (!isSearcherAChild) {
                return -1;
            } else {
                return 0;
            }
        }
        int value;
        while ((value = getRandomNo(number)) == exception);
        return value;
    }
}
