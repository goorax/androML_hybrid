AndroML - Hybrid analysis prototype


<img src="https://raw.githubusercontent.com/goorax/androML_hybrid/master/figures/setup.png" width="300"/><img src="https://raw.githubusercontent.com/goorax/androML_hybrid/master/figures/environment.png" width="300"/>

Tested with Intellij IDEA 2017.2.2
The hybrid analysis uses Elasticsearch as database. The access is configurable via the config file in the resources folder.

- Use Orace Java 8 as default SDK 
- Set the package prefix "androML" for src/main/java/
- Configure Application and use main class "AndroML" and classpath of module "androML_main"
- Use config in resources: androml.properties
- As devices LG Nexus 5 are used with API 23. The need the XPosed framework and an activated Droidmon module.

<img src="https://raw.githubusercontent.com/goorax/androML_hybrid/master/figures/chart_static_info.png" width="300"/><img src="https://raw.githubusercontent.com/goorax/androML_hybrid/master/figures/chart_api_calls.png" width="300"/>
