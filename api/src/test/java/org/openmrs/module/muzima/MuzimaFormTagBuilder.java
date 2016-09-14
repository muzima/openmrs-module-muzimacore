package org.openmrs.module.muzima;

public class MuzimaFormTagBuilder extends Builder<MuzimaFormTag> {
    private Integer id;
    private String name;

    private MuzimaFormTagBuilder() {
    }

    @Override
    public MuzimaFormTag instance() {
        MuzimaFormTag tag = new MuzimaFormTag(name);
        tag.setId(id);
        return tag;
    }

    public static MuzimaFormTagBuilder tag() {
        return new MuzimaFormTagBuilder();
    }


    public MuzimaFormTagBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public MuzimaFormTagBuilder withName(String name) {
        this.name = name;
        return this;
    }
}
