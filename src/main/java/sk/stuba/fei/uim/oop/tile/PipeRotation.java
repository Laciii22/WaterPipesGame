package sk.stuba.fei.uim.oop.tile;


import lombok.Getter;

@Getter
public enum PipeRotation {
    LEFT_UP(0) ,
    RIGHT_UP(1) ,
    RIGHT_DOWN(2) ,
    LEFT_DOWN(3),
    HORIZONTAL(4),
    VERTICAL(5);

    private final int rotation;

    PipeRotation(int rotation) {
        this.rotation = rotation;
    }

    public int getRotation() {
        if (rotation >= 4) {
            return rotation % 2;
        }
        return rotation;
    }
}

