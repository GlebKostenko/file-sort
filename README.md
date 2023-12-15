# Инструкция:

1. [Программа не учитывает](#Программа-не-учитывает)
1. [Тестирование ручным добавлением папок](#Тестирование-ручным-добавлением-папок)
2. [Тестирование с помощью внутренних тестов](#Тестирование-с-помощью-внутренних-тестов)

## Программа не учитывает

1. Программа не ожидает, что в require может стоять что-то кроме файлов доступных к чтению, если передать zip, например,
   никакой ошибки связанной с неверным форматом не предусмотрено
2. Программа не имеет доступа к каким-то зашифрованным папкам, даже если есть все данные, чтобы туда зайти, она просто
   обходит доступные файлы
3. Программе не определяет хэши для сохранения зависимостей, если по какой-то причине вы пытаетесь сохранить файл с
   одним и теж же хэшом корректность программы не гарантируется
4. Программа не содержит отдельных тестов для алгоритма сортировки и проверки зависимостей

## Тестирование ручным добавлением папок

1. Создайте внутри папки проекта ``file-sort/`` нужную файловую структуру  
(можно создать папку или файл в любой директории, включая корневую)
1. Перейдите в директорию проекта
1. Выполните команду ``mvn clean package``
1. Запустите приложение ``java -jar target/file-sort-1.0-SNAPSHOT.jar``
1. Результат будет лежать в ``./result.txt``

#### Или

1. Из IDE запустите Main.main()

## Тестирование с помощью внутренних тестов

Для тестирования используйте класс [TestFileProcessor](./src/test/java/TestFileProcessor.java)  
``getPathFromProjectRoot(File file)`` Возвращает путь до файла относительно проекта(не ссодержит имен, определенных за областью проекта)  
``createTempDir()`` Создаёт три директории  
``createTempFiles()`` Создаёт семь пустых файлов

```bash
├── doczilla_txt
│   ├── one
│   │   ├── two
│   │   │    └── sixth.txt
│   │   ├── fourth.txt
│   │   ├── fifth.txt
│   │   └── illegal.csv
│   ├── one.txt
│   ├── two.txt
│   └── third.txt
```

Тест ``process_whenCycleDependencyExists_thenExceptionShouldBeThrown`` делает следующее:
1. Заполняет файлы текстом, не включающим require
1. Создаёт циклическую зависимость следующим образом:
    1. В файле fourth.txt записывает ``require 'doczilla_txt/one.txt'``
    1. В файле sixth.txt записывает ``require 'doczilla_txt/one/fourth.txt'``
    1. В файле one.txt записывает ``require 'doczilla_txt/one/two/sixth.txt'``
1. Запускает обработку текстовых файлов
1. Ждет, что программа выдаст ``CycleDependencyException``

Тест ``process_whenRequiredFileNotExist_thenPathNotExistExceptionShouldBeThrown`` делает следующее:

1. Заполняет файлы текстом, не включающим require
1. Создаёт зависимость на несуществующий файл следующим образом:
    1. В файле fourth.txt записывает ``require 'random_12345.txt'``
1. Запускает обработку текстовых файлов
1. Ждет, что программа выдаст ``PathNotExistException``

Тест ``process_whenAllIsOk_thenProperOutputShouldBeSeen`` делает следующее:

1. Заполняет файлы текстом, не включающим require
1. Создаёт зависимость на файл следующим образом:
    1. В файле fourth.txt записывает ``require 'doczilla_txt/one.txt'``
1. Запускает обработку текстовых файлов
1. Ждет, что программа успешно выполнится