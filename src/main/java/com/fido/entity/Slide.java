package com.fido.entity;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.BitSet;

@PlanningEntity
public class Slide {
    Slide() { }

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private BitSet tags;

    public BitSet getTags() {
        return tags;
    }

    public void setTags(BitSet tags) {
        this.tags = tags;
    }

    public Slide(String id, BitSet tags) {
        this.id = id;
        this.tags = tags;
    }

    @PlanningVariable(valueRangeProviderRefs = {"slidesRange"})
    private Long orderNumber = 0L;
}
