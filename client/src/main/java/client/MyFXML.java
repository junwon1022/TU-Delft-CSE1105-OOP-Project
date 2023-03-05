/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import com.google.inject.Injector;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import javafx.util.Callback;
import javafx.util.Pair;

public class MyFXML {

    private Injector injector;

    /**
     * Create a new MyFXML instance.
     *
     * @param injector The injector to use.
     */
    public MyFXML(Injector injector) {
        this.injector = injector;
    }

    /*
     * Load a FXML file.
     *
     * @param path The path to the FXML file.
     * @return The root node of the FXML file.
     * @throws IOException If an I/O error occurs.
     */
    public <T> Pair<T, Parent> load(Class<T> c, String... parts) {
        try {
            var loader = new FXMLLoader(getLocation(parts),
                    null, null, new MyFactory(), StandardCharsets.UTF_8);
            Parent parent = loader.load();
            T ctrl = loader.getController();
            return new Pair<>(ctrl, parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Get the location of a FXML file.
     *
     * @param path The path to the FXML file.
     * @return The location of the FXML file.
     */
    private URL getLocation(String... parts) {
        var path = Path.of("", parts).toString();
        return MyFXML.class.getClassLoader().getResource(path);
    }

    /*
     * A custom builder factory.
     */
    private class MyFactory implements BuilderFactory, Callback<Class<?>, Object> {

        /*
         * Create a new builder.
         *
         * @param type The type of the builder.
         * @return The builder.
         */
        @Override
        @SuppressWarnings("rawtypes")
        public Builder<?> getBuilder(Class<?> type) {
            return new Builder() {
                @Override
                public Object build() {
                    return injector.getInstance(type);
                }
            };
        }

        /*
         * Create a new instance.
         *
         * @param type The type of the instance.
         * @return The instance.
         */
        @Override
        public Object call(Class<?> type) {
            return injector.getInstance(type);
        }
    }
}