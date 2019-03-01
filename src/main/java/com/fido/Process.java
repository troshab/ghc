package com.fido;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.fido.entity.Slide;
import com.fido.solution.Slideshow;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Process {

    private static String FILENAME = Config.filename + ".bin";

    private static Map<String, BitSet> slides;
    private static int slidesCount;

    public static void main(String[] args) {
        try {
            System.out.println("Deserialize bin");
            long startTime = System.nanoTime();

            Kryo kryo = new Kryo();
            kryo.addDefaultSerializer(BitSet.class, new BitSetSerializer());

            Input input = new Input(new FileInputStream(FILENAME));
            List<Slide> slides = kryo.readObject(input, LinkedList.class);
            input.close();

            long endTime   = System.nanoTime();
            long totalTime = (endTime - startTime) / 1000 / 1000 / 1000;
            slidesCount = slides.size();
            System.out.println("done in " + totalTime + "s");

            System.out.println("Solving");
            startTime = System.nanoTime();
            Slideshow slideshow = new Slideshow(slides);

            SolverFactory<Slideshow> solverFactory = SolverFactory.createFromXmlResource("solverConfig.xml");
            Solver<Slideshow> solver = solverFactory.buildSolver();

            solver.solve(slideshow);
            System.out.println("done in " + totalTime + "s");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, BitSet> getSlides() {
        return slides;
    }
}
