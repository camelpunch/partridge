(ns ^:figwheel-always partridge.core
    (:require
     [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(def selected-waterway (atom "River Soar Navigation"))

(def waterways ["Trent & Mersey Canal"
                "Shropshire Union Canal"
                "River Soar Navigation"])

(defn home []
  [:div
   [:div#controls
    [:label {:for "waterway"} "Waterway: "]
    [:select#waterway
     {:value @selected-waterway}
     (map (fn [name] [:option name]) waterways)]]
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
