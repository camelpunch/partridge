(ns ^:figwheel-always partridge.core
    (:require
     [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(defonce app-state (atom {:text "Hello world!"}))

(defn home []
  [:div
   [:div#controls
    [:label {:for "waterway"} "Waterway:"]
    [:select#waterway [:option "Trent & Mersey Canal"]]]
   [:div#map-canvas]])

(defn home-did-mount []
  (let [map-canvas (.getElementById js/document "map-canvas")
        map-options (clj->js {:center {:lat 53.0672367 :lng -2.52393}
                        :zoom 9})]
    (js/google.maps.Map. map-canvas map-options)))

(defn home-component []
  (reagent/create-class {:render home
                         :component-did-mount home-did-mount}))

(reagent/render-component [home-component]
                          (. js/document (getElementById "app")))
