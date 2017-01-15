package org.caliog.myRPG.Commands.Utils;

import java.util.ArrayList;
import java.util.List;

public abstract class Commands {

    protected final List<Command> cmds = new ArrayList<Command>();

    public abstract List<Command> getCommands();

}
