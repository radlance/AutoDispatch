import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    let deepLinkHelper = DeepLinkHelper()

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    deepLinkHelper.handleDeepLink(url: url.absoluteString)
                }
        }
    }
}