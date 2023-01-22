(ns diclojure.core
  (:require [diclojure.di :refer [newDIContainer registerPrototype registerSingleton registerJavaSingleton registerJavaPrototype resolveDependency PostConstructable]])
  (:import (diclojurej Car Order CarMaker FactoryManager CurrentOrder SerialNumberManager OrderProvider)))

(defprotocol CarPainter
  (getPaint [this color]))

(defrecord CarPainterImpl []
  CarPainter
  (getPaint [this color]
    (cond
      (= color "red") "#FF0000"
      (= color "blue") "#0000FF"
      (= color "green") "#00FF00"
      )))

(defrecord CarMakerImpl [currentOrder serialNumber carPainter]
  CarMaker
  (fulfillOrder [this]
    (let [
          order (.-order currentOrder)
          color (.-color order)
          paint (.getPaint carPainter color)
          number (.getSerialNumber serialNumber)
          car (Car. number paint)
          ]
      (println "making car " car)
      )))

(defrecord OrderProviderImpl []
  OrderProvider
  (getOrders [this]
    (let [
          order1 (Order. "red")
          order2 (Order. "green")
          order3 (Order. "blue")
          ]
      [order1 order2 order3])
    )
  )

(-> (newDIContainer)
    (registerJavaSingleton CurrentOrder CurrentOrder)
    (registerJavaSingleton SerialNumberManager SerialNumberManager)
    (registerJavaPrototype FactoryManager FactoryManager)
    (registerSingleton OrderProvider ->OrderProviderImpl [])
    (registerPrototype CarMaker ->CarMakerImpl [CurrentOrder SerialNumberManager CarPainter])
    (registerPrototype CarPainter ->CarPainterImpl [])
    (resolveDependency FactoryManager)
    (.fulfillOrders)
    )

