package com.frizo.lab.thread.mysticism.aboutJMM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test2 {

    private static final HashMap<String, Integer> SEATS_MAP = new HashMap<>();

    static {
        SEATS_MAP.put("A", 0);
        SEATS_MAP.put("B", 1);
        SEATS_MAP.put("C", 2);
        SEATS_MAP.put("D", 3);
        SEATS_MAP.put("E", 4);
        SEATS_MAP.put("F", 5);
        SEATS_MAP.put("G", 6);
        SEATS_MAP.put("H", 7);
        SEATS_MAP.put("I", 8);
        SEATS_MAP.put("J", 9);
        SEATS_MAP.put("K", 10);
    }

    private static class Seat {
        private int x;
        private int y;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    public static void main(String[] args) {
        System.out.println(solution(1, ""));
    }


    public static int solution(int N, String S) {
        int count = 0;
        List<Seat> seats = parseSeats(S);
        int[][] array = new int[N][10];

        seats.forEach(seat -> {
            array[seat.getX()][seat.getY()] = 1;
        });

        for(int i=0; i<array.length;++i){
            count += bookIf4Empty(array[i], 1);
            count += bookIf4Empty(array[i], 3);
            count += bookIf4Empty(array[i], 5);
        }
        return count;
    }

    private static int bookIf4Empty(int[] row, int pos) {
        int a = row[pos];
        int b = row[pos+1];
        int c = row[pos+2];
        int d = row[pos+3];
        if (a == 0 && b == 0 && c == 0 && d == 0){
            row[pos] = 1;
            row[pos+1] = 1;
            row[pos+2] = 1;
            row[pos+3] = 1;
            return 1;
        }else {
            return 0;
        }
    }

    private static List<Seat> parseSeats(String s) {
        List<Seat> seats = new ArrayList<>();
        if(!s.equals("")){
            String[] content = s.split(" ");
            for(int i = 0; i < content.length; ++i){
                String[] inner = content[i].split("");
                Seat seat = new Seat();
                seat.setX(Integer.parseInt(inner[0])-1);
                seat.setY(SEATS_MAP.get(inner[1]));
                seats.add(seat);
            }
        }
        return seats;
    }

}
