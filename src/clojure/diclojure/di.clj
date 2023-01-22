(ns diclojure.di
  (:import (diclojurej TestJavaImpl TestJava Test2Java Test2JavaClass)))

(defprotocol PostConstructable
  (postConstruct [this]))

(defprotocol PreDestructable
  (preDestruct [this]))

(defprotocol DependencyRegister
  (registerPrototype [this key factory dependencyKeys])
  (registerSingleton [this key factory dependencyKeys])
  (registerJavaPrototype [this key class])
  (registerJavaSingleton [this key class])
  )

(defprotocol DependencyResolver
  (resolveDependency [this key])
  )

(defn newInstance [class & args]
  (->
    (.getConstructors class)
    (first)
    (.newInstance (into-array Object args))))

(defn getJavaFactory [class] (fn [& args] (apply newInstance (into [class] args))))

(defn getKeysForClass [java-class]
  (.getParameterTypes (first (.getConstructors java-class))))

(defn callPostConstrucIfPresent [java-object]
  (let [
        method (first (filter #(.isAnnotationPresent % javax.annotation.PostConstruct)
                              (.getMethods (.getClass java-object))))
        ] (if (not (= nil method)) (.invoke method java-object (into-array Object [])) nil)))

(defn injectFields [java-object dependencyResolver]
  (let [
        fields (filter #(.isAnnotationPresent % javax.inject.Inject)
                       (.getDeclaredFields (.getClass java-object)))
        ]
    (doseq [field fields]
      (.set field java-object (resolveDependency dependencyResolver (.getType field))))))

(defn processJavaObject [java-object dependencyResolver]
  (let [
        inject (injectFields java-object dependencyResolver)
        postConstruct (callPostConstrucIfPresent java-object)
        ]))

(defprotocol DependencyProvider
  (getDependency [this dependencyResolver]))

(defrecord PrototypeDependencyProvider [factory dependencyKeys]
  DependencyProvider
  (getDependency [_ dependencyResolver]
    (let [object (apply factory (map (fn [key] (resolveDependency dependencyResolver key)) dependencyKeys))
          postConstruct (if (satisfies? PostConstructable object) (postConstruct object))
          javaPostConstruct (if (instance? Object object) (processJavaObject object dependencyResolver))
          ] object)))

(defrecord SingletonDependencyProvider [memoizedFactory]
  DependencyProvider
  (getDependency [_, dependencyResolver] (memoizedFactory dependencyResolver)))

(defn singletonDependencyProvider [factory dependencyKeys]
  (->SingletonDependencyProvider
    (memoize
      (fn [dependencyResolver]
        (getDependency (->PrototypeDependencyProvider factory dependencyKeys) dependencyResolver)))))


(defprotocol DIInner
  (addToMap [this key value])
  )

(defrecord DIContainer [registry]
  DIInner
  (addToMap [_ key value]
    (DIContainer. (assoc registry key value)))
  DependencyRegister
  (registerPrototype [this key factory dependencyKeys]
    (addToMap this key (->PrototypeDependencyProvider factory dependencyKeys)))

  (registerSingleton [this key factory dependencyKeys]
    (addToMap this key (singletonDependencyProvider factory dependencyKeys)))

  (registerJavaPrototype [this key class]
    (registerPrototype this key (getJavaFactory class) (getKeysForClass class)))

  (registerJavaSingleton [this key class]
    (registerSingleton this key (getJavaFactory class) (getKeysForClass class)))


  DependencyResolver
  (resolveDependency [this key]
    (let [dependencyProvider (get registry key)]
      (if (= nil dependencyProvider)
        (println key)
        (getDependency dependencyProvider this))
      )
    )
  )


(defprotocol TestProtocol1
  (testMethod1 [this]))

(defrecord TestRecord1 [s ja]
  TestProtocol1
  (testMethod1 [this] (println "tewstMethod1" s (.getTestMessage ja)))
  )

(defprotocol TestProtocol2
  (testMethod2 [this])
  )

(defrecord TestRecord2 [s testProtocol1 test2javaobj]
  TestProtocol2
  (testMethod2 [this]
    (println "Test method 2 " s " - - "
             (testMethod1 testProtocol1) (.test2java test2javaobj)
             ))
  PostConstructable
  (postConstruct [this] (println "post construction"))
  )

(defn newDIContainer [] (DIContainer. {}))

(defn test []
  (-> (DIContainer. {})
      (registerSingleton :string1 (fn [] "test string 1") '())
      (registerJavaSingleton TestJava TestJavaImpl)
      (registerPrototype TestProtocol2 ->TestRecord2 [:string1 TestProtocol1 Test2Java])
      (registerSingleton TestProtocol1 ->TestRecord1 [:string1 TestJava])
      (registerJavaPrototype Test2Java Test2JavaClass)
      (resolveDependency TestProtocol2)
      (.testMethod2)
      ))

;(test)

