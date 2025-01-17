/*
 * Copyright 2016 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.widgets.client.widget;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class LiteralTextBoxTest {

    private LiteralTextBox textBox;

    @Before
    public void setup() {
        textBox = new LiteralTextBox();
    }

    @Test
    public void testSet() {
        assertEquals("\"", textBox.set("\\\""));
        assertEquals("\\", textBox.set("\\\\"));
        assertEquals("\\\"", textBox.set("\\\\\\\""));
    }

    @Test
    public void testGet() {
        assertEquals("\\\"", textBox.get("\""));
        assertEquals("\\\\", textBox.get("\\"));
        assertEquals("\\\\\\\"", textBox.get("\\\""));
    }
}