# HomiesLib 🏠✨

**HomiesLib** is a custom-built library designed to power **Homies Base**, my personal Minecraft server. This library provides a suite of tools and frameworks to streamline plugin development, making it easier to manage configurations, handle database connections, and create commands—all while maintaining clean and maintainable code.

Whether you're working on a private server or just curious about how Homies Base operates under the hood, HomiesLib is here to make your life easier!

---

## Features 🚀

- **Annotation-Based Configuration Framework**  
  Define and manage configuration files effortlessly using annotations. Set default values, paths, and comments directly in your code.

- **Multi-Database Support**  
  Connect to MySQL, SQLite, or other databases with ease. HomiesLib abstracts the complexity, allowing you to focus on your server's functionality.

- **Command System**  
  Create commands quickly using an annotation-based framework. Define arguments, permissions, and execution logic in a clean and intuitive way.

- **Scheduler Integration**  
  Schedule synchronous and asynchronous tasks seamlessly with built-in support for Bukkit's scheduler.

- **Customizable and Modular**  
  HomiesLib is designed to be flexible. Use only the components you need, or extend the library to fit your server's unique requirements.

- **Optimized for Homies Base**  
  Built specifically for Homies Base, this library ensures smooth performance and reliability for your server's plugins.

---

## Why HomiesLib? ❤️

- **Tailored for Homies Base**: Designed specifically for my server, HomiesLib includes features and optimizations that cater to Homies Base's needs.
- **Easy to Use**: Intuitive APIs and clear documentation make it simple to integrate HomiesLib into your projects.
- **Open Source**: HomiesLib is open for exploration, learning, and collaboration. Feel free to use it as inspiration for your own projects!

```xml
<dependency>
    <groupId>com.github.YourUsername</groupId>
    <artifactId>HomiesLib</artifactId>
    <version>1.0.0</version>
</dependency>
```

##Quick Example
Here's a quick example of how to use HomiesLib's configuration framework:

```java
@ConfigFile(fileName = "config.yml")
public class MyConfig {
    @ConfigKey(path = "welcome-message", comment = "The message shown to new players")
    public String welcomeMessage = "Welcome to Homies Base!";
}

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        ConfigManager configManager = new ConfigManager(this);
        MyConfig config = configManager.loadConfig(MyConfig.class);

        getLogger().info("Welcome Message: " + config.welcomeMessage);
    }
}
```
## Contributing 🤝

We welcome contributions from the community! Whether you're fixing a bug, adding a feature, or improving documentation, your help is appreciated. Here's how you can contribute:

### **1. Fork the Repository**
- Click the **Fork** button at the top right of this repository to create your own copy.

### **2. Clone Your Fork**
- Clone your forked repository to your local machine:
```bash
  git clone https://github.com/YourUsername/HomiesLib.git
```
### **3. Create a New Branch**
Create a new branch for your changes:

```bash
git checkout -b feature/your-feature-name
```

### **4. Make Your Changes**
Make your changes and test them thoroughly.

### **5. Commit Your Changes**
Commit your changes with a clear and descriptive commit message:

```bash

git commit -m "Add: New feature to improve database handling"
```
### **6. Push Your Changes**
Push your changes to your forked repository:

```bash
git push origin feature/your-feature-name
```
### **7. Create a Pull Request**
Go to the [HomiesLib repository](https://github.com/NourEdden-Albishawi/HomiesLib/) and click New Pull Request.

Select your branch and provide a detailed description of your changes.

### **8. Review and Merge**
Your pull request will be reviewed by the maintainers. Once approved, it will be merged into the main branch.

## Guidelines
- Follow the existing code style and conventions.

- Write clear and concise commit messages.

- Test your changes thoroughly before submitting a pull request.

- Be respectful and constructive in all discussions.

## Need Help?
If you have any questions or need help, feel free to open an issue or join our [Discord server](https://discord.gg/3RDebsPMQG).

Thank you for contributing to HomiesLib! 🎉

---
## License 📜
HomiesLib is licensed under the MIT License. See the [LICENSE](https://github.com/NourEdden-Albishawi/HomiesLib/blob/master/LICENSE) file for more details.

## About Homies Base 🏠
Homies Base is a private Minecraft server where friends come together to build, explore, and have fun. HomiesLib is the backbone of many plugins that make this server unique and enjoyable.

**HomiesLib**: The secret sauce behind Homies Base. 🏠✨


---
