package edu.cnm.deepdive.games.crapssimulator;

import android.content.Context;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Game {

  private Context context;
  private Random rng;
  private State state;
  private int point;
  private long plays;
  private long wins;
  private List<Roll> rolls;



  public State getState() {
    return state;
  }

  public void setRng(Random rng) {
    this.rng = rng;
  }

  public List<Roll> getRolls() {
    return rolls;
  }

  public int getPoint() {
    return point;
  }

  public long getPlays() {
    return plays;
  }

  public long getWins() {
    return wins;
  }

  public long getLosses() {
    return plays - wins;
  }


  public Game(Context context) {
    this.context = context;
    plays = 0;
    wins = 0;
    rolls = new LinkedList<>();
    reset();
  }

  public void roll() {
    if (state != State.COME_OUT && state != State.POINT) {
      return;
    }
    Roll roll = (state == State.POINT) ? new Roll(point) : new Roll();
    if (point == 0 && roll.after == State.POINT) {
      point = roll.dice[0] + roll.dice[1];
    }
    state = roll.after;
    rolls.add(roll);
    if (state == State.WIN || state == State.LOSE) {
      plays++;
      if (state == State.WIN) {
        wins++;
      }
    }

  }

  public void play() {
    while (state != State.WIN && state != State.LOSE) {
      roll();
    }
  }

  public void reset() {
    state = State.COME_OUT;
    point = 0;
    rolls.clear();
  }

  public class Roll { // can't see non-static fields if static

    public final State before;
    public final State after;
    public final int[] dice = {
        rng.nextInt(6) + 1,
        rng.nextInt(6) + 1
    };

    private Roll() {
      before = State.COME_OUT;
      after = before.newState(dice[0] + dice[1]);
    }
    private Roll(int point) {
      before = State.POINT;
      after = before.newState(dice[0] + dice[1], point);
    }

    @Override
    public String toString() {
      return String.format(context.getString(R.string.dice_roll_message), before, Arrays.toString(dice), after);
    }
  }


  public enum State {

    // "{}" body of a class and I can override methods inside of them. look below
    // Anonymous class
    COME_OUT {
      @Override
      public State newState(int roll) {
        switch (roll) {
          case 7:
          case 11:
            return WIN;
          case 2:
          case 3:
          case 12:
            return LOSE;
          default:
            return POINT;
        }
      }
      @Override
      public State newState(int roll, int point) {
        return newState(roll);
      }
    },

    POINT {
      @Override // "Code" in pulldown menu above and then "Override" while carriage is in between curly braces.
      public State newState(int roll)
          throws IllegalArgumentException
      {
        if (roll != 7) {
          // DO something bad
          throw new IllegalArgumentException();
        }
        return LOSE;
      }

      @Override
      public State newState(int roll, int point) {
        if (roll == point) {
          return WIN;
        } else if (roll == 7) {
          return LOSE;
        }
        return POINT;

      }
    },

    WIN,
    LOSE; // add a semi-colon if I'm adding members to each enum, enums are also always static.

    public State newState(int roll) {
      return this;
    }
    // overloading the state method
    public State newState(int roll, int point) {
      return this;

    }
  }



}
