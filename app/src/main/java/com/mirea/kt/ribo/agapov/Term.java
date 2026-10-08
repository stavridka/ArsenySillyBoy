package com.mirea.kt.ribo.agapov;

public class Term {

    private long id;
    private String name;
    private String definition;
    private boolean favorite;

    public Term(long id, String name, String definition, boolean favorite) {
        this.id = id;
        this.name = name;
        this.definition = definition;
        this.favorite = favorite;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDefinition() {
        return definition;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
