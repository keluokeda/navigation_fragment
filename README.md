# navigation_fragment
[项目地址](https://github.com/keluokeda/navigation_fragment)
### 问题
网上看到很多BottomNavigation+Fragment的例子，写得都很好，但是都没有做状态保存。一旦发生内存回收或者Activity旋转，之前Activity保存的Fragment就会和显示的Fragment重叠。![Fragment重叠](https://upload-images.jianshu.io/upload_images/3690197-ef7db92e692631bc.gif?imageMogr2/auto-orient/strip)

### 问题原理
出现重叠的原因很简单，在Activity回收的时候，会帮我们保存显示的Fragment的实例，恢复的时候会自动帮我们显示出来。但是我们没做处理，我们要求Activity显示的Fragment和Activity帮我们恢复的Fragment就重叠在一起了。

### 解决办法
那么，这个问题要怎么解决呢？这里分两个情况
#### 单Activity单fragment
这个就简单了，在**onCreate**中判断，如果**savedInstanceState**为空就显示，不为空就不做任何处理。
```
 override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction().add(R.id.fragment_container,HomeFragment()).commit()
        }
}
```
#### 单Activity+多Fragment
比较常见的是上面说的BottomNavigationView+Fragment方案，
解决方案大致如下：
##### 1，不要直接创建Fragment，先根据findFragmentByTag找缓存的fragment，找不到就创建
```
 private fun initFragmentList() {
        fragmentList.add(
                supportFragmentManager.findFragmentByTag(HomeFragment::class.java.name)
                        ?: HomeFragment())

        fragmentList.add(supportFragmentManager.findFragmentByTag(TabFragment::class.java.name)
                ?: TabFragment())
    }
```
##### 2，添加的时候加上Fragment的类名作为Tag
```
  transaction.add(R.id.fragment_container, current, current.javaClass.name)

```

##### 3，同时注意保存上次显示的Fragment的下标。

完整代码如下
```

class MainActivity : AppCompatActivity() {
    /**
     * 上次显示deFragment的下标
     */
    var lastFragmentIndex = -1
    private val fragmentList = mutableListOf<Fragment>()

    private val bottomNavigationView: BottomNavigationView by lazy {
        findViewById(R.id.bottom_navigation_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lastFragmentIndex = savedInstanceState?.getInt("lastFragmentIndex", -1) ?: -1

        Logger.d("MainActivity onCreate ${savedInstanceState == null} lastFragmentIndex = $lastFragmentIndex")

        initFragmentList()

        bottomNavigationView.setOnNavigationItemSelectedListener {
            if (it.itemId == R.id.action_home) {
                showFragment(0)
            } else {
                showFragment(1)
            }

            return@setOnNavigationItemSelectedListener true
        }
        if (lastFragmentIndex == -1)
            bottomNavigationView.selectedItemId = R.id.action_home
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("lastFragmentIndex", lastFragmentIndex)
    }

    private fun initFragmentList() {
        fragmentList.add(
                supportFragmentManager.findFragmentByTag(HomeFragment::class.java.name)
                        ?: HomeFragment())

        fragmentList.add(supportFragmentManager.findFragmentByTag(TabFragment::class.java.name)
                ?: TabFragment())
    }

    private fun showFragment(index: Int) {
        if (index < 0 || index >= fragmentList.size) {
            return
        }

        val transaction = supportFragmentManager.beginTransaction()
        if (lastFragmentIndex >= 0 && lastFragmentIndex < fragmentList.size) {
            transaction.hide(fragmentList[lastFragmentIndex])
        }
        val current = fragmentList[index]
        if (!current.isAdded) {
            //添加Fragment到Activity并设置Tag，方便重启之后找
            transaction.add(R.id.fragment_container, current, current.javaClass.name)
        }
        transaction.show(current)
        transaction.commit()
        lastFragmentIndex = index
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menu.add(0, 1, 0, "重启").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 1) {
            recreate()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
```
