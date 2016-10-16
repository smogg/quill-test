(ns quill-test.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [cljsjs.quill]))

(def quill js/Quill)

(defn wysiwyg-render
  []
  [:div {:id (gensym)
         :class "wysiwyg"}])

(defn wysiwyg-did-mount
  [this]
  (let [dom-node (reagent/dom-node this)
        settings {:modules {:toolbar [[{:header [1 2 false]}]
                                      ["bold" "italic" "underline"]]}
                  :theme "snow"}]
    (quill. dom-node (clj->js settings))))

(defn wysiwyg-component
  []
  (reagent/create-class {:reagent-render wysiwyg-render
                         :component-did-mount wysiwyg-did-mount}))

;; -------------------------
;; Views
(defn home-page []
  [:div [:h2 "Welcome to quill-test"]
   [:div#dummy-div]
   [wysiwyg-component]
   [:div [:a {:href "/about"} "go to about page"]]])

(defn about-page []
  [:div [:h2 "About quill-test"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
