(ns app.ui
  (:require
   [app.mutations :as api]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]))

(defsc Person [_ {:person/keys [id name age] :as props} {:keys [onDelete]}]
  {:query         [:person/id :person/name :person/age] ; (2)
   :ident         (fn [] [:person/id (:person/id props)]) ; (1)
   :initial-state (fn [{:keys [id name age]}] {:person/id id :person/name name :person/age age})} ; (3)
  (dom/li
   (dom/h5 (str name " (age: " age ")") (dom/button {:onClick #(onDelete id)} "X")))) ; (4)

(def ui-person (comp/factory Person {:keyfn :person/id}))

(defsc PersonList [this {:list/keys [id label people] :as props}]
  {:query [:list/id :list/label {:list/people (comp/get-query Person)}] ; (5)
   :ident (fn [] [:list/id (:list/id props)])}
  (let [delete-person (fn [person-id] (comp/transact! this [(api/delete-person {:list/id id :person/id person-id})]))] ; (4)
    (dom/div
     (dom/h4 label)
     (dom/ul
      (map #(ui-person (comp/computed % {:onDelete delete-person})) people)))))

(def ui-person-list (comp/factory PersonList))

(defsc Root [_ {:keys [friends enemies]}]
  {:query         [{:friends (comp/get-query PersonList)}
                   {:enemies (comp/get-query PersonList)}]
   :initial-state {}}
  (dom/div
   
   (dom/h3 "Friends")
   (when friends
     (ui-person-list friends))
   
   (dom/h3 "Enemies")
   (when enemies
     (ui-person-list enemies))))
