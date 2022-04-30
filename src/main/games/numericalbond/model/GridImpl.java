package main.games.numericalbond.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import main.games.numericalbond.utility.Position;
import main.games.numericalbond.utility.PositionImpl;

/**
 * Class that represents a (square) grid of blocks.
 */
public class GridImpl implements Grid {

    private static final int MAX_LINKS_PER_BLOCK = 99;

    private final int size;
    private final Map<Position, Block> blocks;

    /**
     * Builds a new {@link GridImpl}.
     * @param size
     *          the size of the square grid
     */
    public GridImpl(final int size) {
        this.size = size;
        this.blocks = new HashMap<>();
        IntStream.range(0, this.size)
                .forEach(i -> IntStream.range(0, this.size)
                        .forEach(j -> this.blocks.put(new PositionImpl(i, j), new BlockImpl(MAX_LINKS_PER_BLOCK))));
    }

    /**
     * Builds a new {@link GridImpl}.
     * @param size
     *          the size of the square grid
     * @param blocks
     *          the map of the blocks that the grid will have
     */
    public GridImpl(final int size, final Map<Position, Block> blocks) {
        Objects.requireNonNull(blocks);
        this.size = size;
        this.blocks = Map.copyOf(blocks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return this.size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Position, Block> getBlocks() {
        return Map.copyOf(this.blocks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLegal(final Position position) {
        return position != null
                && position.getX() >= 0 && position.getX() < this.size
                && position.getY() >= 0 && position.getY() < this.size;
    }

    private void requireLegalPosition(final Position position) {
        if (!isLegal(position)) {
            throw new IllegalArgumentException("Illegal position");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Block getBlockAt(final Position position) {
        requireLegalPosition(position);
        return this.blocks.get(position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Position> getNearbyPosition(final Position position, final Direction direction) {
        requireLegalPosition(position);
        Objects.requireNonNull(direction);
        if (isLegal(direction.getPosition(position))) {
            return Optional.of(direction.getPosition(position));
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Direction> getDirection(final Position pos1, final Position pos2) {
        requireLegalPosition(pos1);
        requireLegalPosition(pos2);
        for (final Direction d : Direction.getDirections()) {
            if (d.getPosition(pos1).equals(pos2)) {
                return Optional.of(d);
            }
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canLink(final Position pos1, final Position pos2) {
        return !getDirection(pos1, pos2).isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void link(final Position pos1, final Position pos2) {
        if (!canLink(pos1, pos2)) {
            throw new IllegalStateException("Can't link");
        }
        final Optional<Direction> directionFrom1To2 = getDirection(pos1, pos2);
        this.blocks.get(pos1).link(directionFrom1To2.get());
        this.blocks.get(pos2).link(directionFrom1To2.get().opposite());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLinks(final Position pos1, final Position pos2) {
        final Optional<Direction> directionFrom1To2 = getDirection(pos1, pos2);
        if (directionFrom1To2.isEmpty()) {
            throw new IllegalArgumentException("Positions aren't adjacent");
        }
        return this.blocks.get(pos1).getLinks(directionFrom1To2.get());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isComplete() {
        for (final Block b : this.blocks.values()) {
            if (b.getCurrentLinks() != b.getLinksToHave()) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "GridImpl [size=" + this.size + ", blocks=" + this.blocks + "]";
    }

}
