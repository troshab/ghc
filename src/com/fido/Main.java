package com.fido;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class Main {

    static Integer tagsID_AI = 0;
    static Map<String, Integer> tagsID = new HashMap<>();

    static Integer photosID_AI = 0;
    static Map<String, BitSet> verticalPhotos = new HashMap<>();
    static Map<String, BitSet> slides = new HashMap<>();

    static Integer getTagID(String word) {
        Integer tagID = tagsID.get(word);
        if (tagID == null) {
            tagsID.put(word, tagsID_AI);
            tagsID_AI++;
            return tagsID_AI - 1;
        }
        return tagID;
    }

    public static void main(String[] args) {
        try(FileInputStream fin = new FileInputStream("d:\\tds\\b_lovely_landscapes.txt")) {
            long startTime = System.nanoTime();
            int i;
            while((i = fin.read()) != -1){
                if ((char) i == '\n') {
                    break;
                }
            }
            boolean isHorizontal = false;
            String word = "";

            int tagsCountPromised = 0;
            int tagsCountFinded = 0;
            int state = 0;
            BitSet tags = new BitSet();

            while((i = fin.read()) != -1){
                if ((char) i == ' ') {
                    if (state == 2) {
                        Integer tagId = getTagID(word);
                        tags.set(tagId);
                        //System.out.println("tag #" + tagId + ": " + word);
                        tagsCountFinded++;
                    } else if (state == 1) {
                        tagsCountPromised = Integer.parseInt(word);
                        state = 2;
                    } else {
                        if (word.equals("H")) {
                            isHorizontal = true;
                        }
                        state = 1;
                    }
                    word = "";
                } else if ((char) i == '\n') {
                    Integer tagId = getTagID(word);
                    tags.set(tagId);
                    //System.out.println("tag #" + tagId + ": " + word);
                    tagsCountFinded++;
                    if (tagsCountFinded != tagsCountPromised) {
                        System.out.flush();
                        System.err.println("tags count mismatch, promised " + tagsCountPromised + " but finded " + tagsCountFinded);
                        System.err.flush();
                        System.exit(-1);
                    } else {
                        if (isHorizontal) {
                            slides.put(photosID_AI.toString(), tags);
                        } else {
                            verticalPhotos.put(photosID_AI.toString(), tags);
                        }
                        //System.out.println("tags count match, promised " + tagsCountPromised + " finded " + tagsCountFinded);
                    }

                    word = "";
                    tagsCountPromised = 0;
                    tagsCountFinded = 0;
                    state = 0;
                    tags = new BitSet();
                    isHorizontal = false;

                    photosID_AI++;
                } else {
                    word += (char) i;
                }
            }
            long endTime   = System.nanoTime();
            long totalTime = (endTime - startTime) / 1000000;
            System.out.print("Time: " + totalTime + "s");
        } catch(IOException exception){
            exception.printStackTrace();
        }
        for(Map.Entry<String, BitSet> verticalPhoto1 : verticalPhotos.entrySet()) {
            for(Map.Entry<String, BitSet> verticalPhoto2 : verticalPhotos.entrySet()) {
                if (!verticalPhoto1.equals(verticalPhoto2)) {
                    BitSet twoVerticalPhotoTags = verticalPhoto1.getValue();
                    twoVerticalPhotoTags.or(verticalPhoto2.getValue());
                    Integer verticalPhoto1ID = Integer.valueOf(verticalPhoto1.getKey());
                    Integer verticalPhoto2ID = Integer.valueOf(verticalPhoto2.getKey());
                    slides.put(verticalPhoto1ID < verticalPhoto2ID ? verticalPhoto1.getKey() + " " + verticalPhoto2.getKey() : verticalPhoto2.getKey() + " " + verticalPhoto1.getKey(), twoVerticalPhotoTags);
                }
            }
        }
        System.out.println("DONE");
    }
}
