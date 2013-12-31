/**
 * Copyright (C) 2011 PROCESSBASE Ltd.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, see <http://www.gnu.org/licenses/>.
 */
package com.schdri.bpm.bonita.diagram;

import java.util.HashMap;

/**
 *
 * @author mgubaidullin
 */
public class Process {

    private String name;
    private String state;
    private int x = 40;
    private int y = 40;
    private int width = 0;
    private int height = 0;
    private HashMap<String, Step> steps = new HashMap<String, Step>();

    public Process(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void addStep(String key, Step step) {
        this.steps.put(key, step);
    }

    public HashMap<String, Step> getSteps() {
        return steps;
    }

    public void setSteps(HashMap<String, Step> steps) {
        this.steps = steps;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
