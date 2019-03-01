package com.fido.solution;

import com.fido.entity.Slide;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@PlanningSolution
@XStreamAlias("Slideshow")
public class Slideshow {

    public Slideshow() { }

    public Slideshow(List<Slide> slides) {
        this.slides = slides;
    }

    private List<Slide> slides = new LinkedList<>();

    @ProblemFactCollectionProperty
    public List<Slide> getSlides() {
        return slides;
    }

    public void setSlides(List<Slide> slides) {
        this.slides = slides;
    }

    private List<Slide> slideshow;

    @PlanningEntityCollectionProperty
    public List<Slide> getSlideshow() {
        return slideshow;
    }

    public void setSlideshow(List<Slide> slideshow) {
        this.slideshow = slideshow;
    }


    private SimpleScore score;

    @PlanningScore
    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

    @ValueRangeProvider(id = "slidesRange")
    @ProblemFactCollectionProperty
    public List<Long> getSlidesCount() {
        return LongStream.range(0, slides.size()).boxed().collect(Collectors.toList());
    }
}
