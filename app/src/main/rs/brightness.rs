/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#pragma version(1)
#pragma rs java_package_name(zwaggerboyz.instaswaggify)
#pragma rs_fp_relaxed

const static float3 gMonoMult = {0.299f, 0.587f, 0.114f};

float brightnessValue = 0.f;

/*
RenderScript kernel that performs saturation manipulation.
*/
uchar4 __attribute__((kernel)) brightness(uchar4 in)
{
    float4 f4 = rsUnpackColor8888(in);

    /* brightness calculations */
    float val = f4.r + f4.g + f4.b;

    f4.r = f4.r * brightnessValue;
    f4.g = f4.g * brightnessValue;
    f4.b = f4.b * brightnessValue;

    /* clipping check */
    if(f4.r > 1.0) f4.r = 1.0f;
    if(f4.g > 1.0) f4.g = 1.0f;
    if(f4.b > 1.0) f4.b = 1.0f;

    float3 result = {f4.r, f4.g, f4.b};

    return rsPackColorTo8888(result);
}