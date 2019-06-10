public class HelloWorld {
    public static void main(String[] args) {
        //Total lines of code as of 5/26/2019: 4337
        //known bugs:
        //unneeded debugging print statements exist
        int below = 0;
        int above = 0;
        for (int i = 0; i < 1000; i++) {
            double rand = Math.random();
            if (rand < 0.15) {
                below++;
            } else {
                above++;
            }
            System.out.println(0.15 - (double) below / (double) (i + 1));
        }
    }
}
