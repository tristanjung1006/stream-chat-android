/*
 * Copyright (c) 2014-2022 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-chat-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.chat.android.compose.ui.attachments.factory

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import io.getstream.chat.android.compose.ui.attachments.AttachmentFactory
import io.getstream.chat.android.compose.ui.attachments.content.ImageAttachmentContent
import io.getstream.chat.android.compose.ui.attachments.content.ImageAttachmentPreviewContent
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.util.isMedia

/**
 * An [AttachmentFactory] that validates attachments as images and uses [ImageAttachmentContent] to
 * build the UI for the message.
 */
@Deprecated(
    message = "Deprecated in favor of `MediaAttachmentFactory`. The new factory is able to" +
        "preview video content as well as images and has access to the new and improved media gallery.",
    replaceWith = ReplaceWith(
        expression = "MediaAttachmentFactory()",
        "io.getstream.chat.android.compose.ui.attachments.factory.MediaAttachmentFactory"
    ),
    level = DeprecationLevel.WARNING
)
@Suppress("FunctionName")
public fun ImageAttachmentFactory(): AttachmentFactory = AttachmentFactory(
    canHandle = { attachments -> attachments.all { it.isMedia() } },
    previewContent = { modifier, attachments, onAttachmentRemoved ->
        ImageAttachmentPreviewContent(
            modifier = modifier,
            attachments = attachments,
            onAttachmentRemoved = onAttachmentRemoved
        )
    },
    content = @Composable { modifier, state ->
        ImageAttachmentContent(
            modifier = modifier
                .width(ChatTheme.dimens.attachmentsContentImageWidth)
                .wrapContentHeight()
                .heightIn(max = ChatTheme.dimens.attachmentsContentImageMaxHeight),
            attachmentState = state
        )
    },
)
