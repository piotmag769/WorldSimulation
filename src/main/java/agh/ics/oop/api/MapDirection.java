package agh.ics.oop.api;

public enum MapDirection {
    NORTH,
    SOUTH,
    EAST,
    WEST,
    NORTH_EAST,
    SOUTH_EAST,
    SOUTH_WEST,
    NORTH_WEST,
    GRASS;

    @Override
    public String toString()
    {
        return switch (this) {
            case NORTH -> "N";
            case SOUTH -> "S";
            case EAST -> "E";
            case WEST -> "W";
            case NORTH_EAST -> "NE";
            case SOUTH_EAST -> "SE";
            case SOUTH_WEST -> "SW";
            case NORTH_WEST -> "NW";
            default -> "*";
        };
    }

    public MapDirection next()
    {
        return switch (this) {
            case NORTH -> NORTH_EAST;
            case NORTH_EAST -> EAST;
            case EAST -> SOUTH_EAST;
            case SOUTH_EAST -> SOUTH;
            case SOUTH -> SOUTH_WEST;
            case SOUTH_WEST -> WEST;
            case WEST -> NORTH_WEST;
            case NORTH_WEST -> NORTH;
            default -> GRASS;
        };
    }

    public MapDirection previous()
    {
        return switch (this) {
            case NORTH -> NORTH_WEST;
            case NORTH_WEST -> WEST;
            case WEST -> SOUTH_WEST;
            case SOUTH_WEST -> SOUTH;
            case SOUTH -> SOUTH_EAST;
            case SOUTH_EAST -> EAST;
            case EAST -> NORTH_EAST;
            case NORTH_EAST -> NORTH;
            default -> GRASS;
        };
    }

    public Vector2d toUnitVector()
    {
        return switch (this) {
            case NORTH -> new Vector2d(0, 1);
            case SOUTH -> new Vector2d(0, -1);
            case EAST -> new Vector2d(1, 0);
            case WEST -> new Vector2d(-1, 0);
            case NORTH_EAST -> new Vector2d(1, 1);
            case SOUTH_EAST -> new Vector2d(1, -1);
            case SOUTH_WEST -> new Vector2d(-1, -1);
            case NORTH_WEST -> new Vector2d(-1, 1);
            default -> null;
        };
    }

    public String getImagePath()
    {
        return switch(this)
                {
                    case NORTH -> "/jeze/N.jpg";
                    case EAST -> "/jeze/E.jpg";
                    case WEST -> "/jeze/W.jpg";
                    case SOUTH -> "/jeze/S.jpg";
                    case NORTH_EAST -> "/jeze/NE.jpg";
                    case SOUTH_EAST -> "/jeze/SE.jpg";
                    case SOUTH_WEST -> "/jeze/SW.jpg";
                    case NORTH_WEST -> "/jeze/NW.jpg";
                    default -> "/grass.jpg";
                };
    }
}