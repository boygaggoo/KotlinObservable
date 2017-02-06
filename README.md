# KotlinObservable
A set of data structures to get rid of adapters

example:
```
val strings=ObservableArrayList<String>()
val columnCount=ObservableField(1)
val list by lazy { findViewById(R.id.list) as RecyclerView? }

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    list?.bind(columnCount){layoutManager=GridLayoutManager(this,it)}
    list?.bind(files){parent,viewType->
            LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_1,parent,false)
        }?.onBindView {
            val textView=it.itemView.findViewById(android.R.id.text1) as TextView
            text.textView=strings[it.adapterPosition]
        }
}
```
Step 1. Add the JitPack repository to your build file
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Step 2. Add the dependency
```
dependencies {
    compile 'com.github.yzheka:KotlinObservable:1.0'
}
```
