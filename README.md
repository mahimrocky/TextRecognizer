# TextRecognizer
This library extend [google vision](https://developers.google.com/vision/) . And initilay it read text from image.
for reading text from image you have to give image **Uri** or **Bitmap**.

Sample
![Alt Text](https://github.com/mahimrocky/TextRecognizer/blob/master/screenshot.png)
# Setup
Setup part is simple

# Root Gradle
```sh
    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

# App Gradle:

```sh
dependencies {
	        implementation 'com.github.mahimrocky:TextRecognizer:1.0.0'
	}
```

# Api 
```sh
 TextScanner.getInstance(this)
                .init()
                .load(uri) // uri or bitmap
                .getCallback(new TextExtractCallback() {
                    @Override
                    public void onGetExtractText(List<String> textList) {
                        // Here ypu will get list of text

                    }
                });
```
Happy coding
