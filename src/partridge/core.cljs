(ns ^:figwheel-always partridge.core
    (:require
     [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn home []
  [:div [:h1 "Waterway to explore the canals!"]
   [:div#map-canvas]])

(defn home-did-mount []
  (let [map-canvas (.getElementById js/document "map-canvas")
        map-options (clj->js {"center" (google.maps.LatLng. -34.397 150.644)
                              "zoom" 8})]
    (js/google.maps.Map. map-canvas map-options)))

(defn home-component []
  (reagent/create-class {:render home
                         :component-did-mount home-did-mount}))

(reagent/render-component [home-component]
                          (. js/document (getElementById "app")))
