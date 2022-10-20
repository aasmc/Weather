# Weather

Educational project that is based on Instant Weather https://github.com/mayokunadeniyi/Instant-Weather.

At this step I rewrote the app from scratch and plan to refactor it to use DataStore, Kotlin Flow, Jetpack Compose, 
Clean Architecture. 

## TODO List
- Refactor preferences to Flow DONE
- Use Kotlin Flows for observing changes DONE (reject in favor of suspend functions)
- Refactor to Clean Architecture DONE
- Introduce unidirectional data flow DONE
- test app components
- Refactor to use Jetpack Compose for UI (make sure there's no flickering when list of forecasts is updated)
- Add WorkManager work to clear DB cache depending on cache duration from preferences
- Create widget for the app
- Add animations

```text
MIT License

Copyright (c) 2020 Mayokun Adeniyi

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```